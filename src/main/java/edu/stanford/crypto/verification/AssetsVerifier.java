/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.BlockingExecutor;
import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.bitcoin.SQLBlockchain;
import edu.stanford.crypto.proof.assets.AddressProof;
import edu.stanford.crypto.proof.assets.AddressProofEntry;
import edu.stanford.crypto.proof.assets.AssetProof;
import edu.stanford.crypto.proof.binary.BinaryProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class AssetsVerifier
        implements Verifier<AssetProof, GeneratorData<AssetProof>> {
    private final AddressProofVerifier addressVerifier = new AddressProofVerifier();
    private final BlockingQueue<AddressProofEntry> proofsQueue = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 3);
    private final AtomicReference<ECPoint> totalEncryption = new AtomicReference<>(ECConstants.INFINITY);

    @Override
    public void verify(AssetProof proof, GeneratorData<AssetProof> data) {
        Iterator<AddressProofEntry> addressProofs = proof.getAddressProofs();
        BlockingExecutor pool = new BlockingExecutor();
        int nThreads = Runtime.getRuntime().availableProcessors() - 1;
        Stream.generate(AssetsVerificationWorker::new).limit(nThreads).forEach(pool::submit);

        try (SQLBlockchain blockchain = new SQLBlockchain()) {
            AddressProofEntry addressProofEntry;
            while (addressProofs.hasNext()) {
                addressProofEntry = addressProofs.next();
                if (this.proofsQueue.offer(addressProofEntry)) continue;
                this.verifyAddressProof(blockchain, addressProofEntry);
            }
            while ((addressProofEntry = this.proofsQueue.poll()) != null) {
                this.verifyAddressProof(blockchain, addressProofEntry);
            }
            pool.shutdownNow();
            pool.awaitTermination(1, TimeUnit.MINUTES);
            System.out.println("Total Encryption " + this.totalEncryption.get().normalize());


        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't complete proof");
        }
    }

    private void verifyAddressProof(SQLBlockchain blockchain, AddressProofEntry addressProofEntry) {
        ECPoint publicKey = addressProofEntry.getPublicKey();
        BigInteger balance = blockchain.getBalance(publicKey);
        AddressVerificationData addressVerificationData = new AddressVerificationData(publicKey, balance, ECConstants.G, ECConstants.H);
        AddressProof addressProof = addressProofEntry.getAddressProof();
        this.addressVerifier.verify(addressProof, addressVerificationData);
        this.totalEncryption.accumulateAndGet(addressProof.getCommitmentBalance(), ECPoint::add);
    }

    private class AssetsVerificationWorker
            implements Runnable {
        private AssetsVerificationWorker() {
        }

        @Override
        public void run() {
            try (SQLBlockchain blockchain = new SQLBlockchain()) {

                while (!Thread.interrupted()) {
                    try {
                        AddressProofEntry addressProofEntry = AssetsVerifier.this.proofsQueue.take();
                        AssetsVerifier.this.verifyAddressProof(blockchain, addressProofEntry);
                    } catch (InterruptedException e) {
                        // empty catch block
                        break;
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new AssertionError(e);
            }
        }
    }

}

