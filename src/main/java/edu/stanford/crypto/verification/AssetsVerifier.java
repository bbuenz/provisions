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
    private final BinaryProofVerifier binaryProofVerifier = new BinaryProofVerifier();
    private final BlockingQueue<AddressProofEntry> proofsQueue = new ArrayBlockingQueue<AddressProofEntry>(Runtime.getRuntime().availableProcessors() * 3);
    private final AtomicReference<ECPoint> totalEncryption = new AtomicReference<ECPoint>(ECConstants.INFINITY);

    @Override
    public void verify(AssetProof proof, GeneratorData<AssetProof> data) {
        Iterator<AddressProofEntry> addressProofs = proof.getAddressProofs();
        BlockingExecutor pool = new BlockingExecutor();
        int nThreads = Runtime.getRuntime().availableProcessors() - 1;
        Stream.generate(() -> new AssetsVerificationWorker()).limit(nThreads).forEach(pool::submit);
        try {
            SQLBlockchain blockchain = new SQLBlockchain();
            Throwable throwable = null;
            try {
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
            }
            catch (Throwable addressProofEntry) {
                throwable = addressProofEntry;
                throw addressProofEntry;
            }
            finally {
                if (blockchain != null) {
                    if (throwable != null) {
                        try {
                            blockchain.close();
                        }
                        catch (Throwable var9_12) {
                            throwable.addSuppressed(var9_12);
                        }
                    } else {
                        blockchain.close();
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
        catch (InterruptedException e) {
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
        BinaryProof binaryProof = addressProofEntry.getBinaryProof();
        this.binaryProofVerifier.verify(binaryProof, new GeneratorData<BinaryProof>());
        this.totalEncryption.accumulateAndGet(addressProof.getCommitmentBalance(), ECPoint::add);
    }

    private class AssetsVerificationWorker
    implements Runnable {
        private AssetsVerificationWorker() {
        }

        @Override
        public void run() {
            try {
                SQLBlockchain blockchain = new SQLBlockchain();
                Throwable throwable = null;
                try {
                    while (!Thread.interrupted()) {
                        try {
                            AddressProofEntry addressProofEntry = (AddressProofEntry)AssetsVerifier.this.proofsQueue.take();
                            AssetsVerifier.this.verifyAddressProof(blockchain, addressProofEntry);
                        }
                        catch (InterruptedException e) {
                            // empty catch block
                            break;
                        }
                    }
                }
                catch (Throwable e) {
                    throwable = e;
                    throw e;
                }
                finally {
                    if (blockchain != null) {
                        if (throwable != null) {
                            try {
                                blockchain.close();
                            }
                            catch (Throwable e) {
                                throwable.addSuppressed(e);
                            }
                        } else {
                            blockchain.close();
                        }
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
                throw new AssertionError(e);
            }
        }
    }

}

