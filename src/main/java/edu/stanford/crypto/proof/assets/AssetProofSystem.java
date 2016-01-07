/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.bitcoin.BlockchainEntry;
import edu.stanford.crypto.bitcoin.PrivateKeyDatabase;
import edu.stanford.crypto.bitcoin.SQLBlockchain;
import edu.stanford.crypto.bitcoin.SQLPrivateKeyDatabase;
import edu.stanford.crypto.proof.ProofSystem;
import edu.stanford.crypto.proof.binary.BinaryProof;
import edu.stanford.crypto.proof.binary.BinaryProofData;
import edu.stanford.crypto.proof.binary.BinaryProofSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class AssetProofSystem
        implements ProofSystem<AssetProof, AssetProofData> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final AddressProofSystem addressProofSystem = new AddressProofSystem();
    private final BlockingQueue<BlockchainEntry> blockchainEntriesQueue = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 3);
    private final BinaryProofSystem binaryProofSystem = new BinaryProofSystem();

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public AssetProof createProof(AssetProofData data) {
        try {
            SQLBlockchain blockchain = new SQLBlockchain();
            SQLPrivateKeyDatabase privateKeyDatabase = new SQLPrivateKeyDatabase();
            try {
                AssetProof proof;
                ExecutorService executorService;
                Iterator<BlockchainEntry> blockchainEntries = blockchain.getBlockchainEntries();
                proof = new AssetProof();
                int nThreads = Runtime.getRuntime().availableProcessors() - 1;
                executorService = Executors.newFixedThreadPool(nThreads);
                proof.getConnection().setAutoCommit(false);
                Stream.generate(AddressProofWorkerThread::new).limit(nThreads).forEach(executorService::submit);
                BlockchainEntry blockchainEntry;
                while (blockchainEntries.hasNext()) {
                    blockchainEntry = blockchainEntries.next();
                    if (this.blockchainEntriesQueue.offer(blockchainEntry)) continue;
                    this.createAddressProof(blockchainEntry, proof, privateKeyDatabase);
                }
                while ((blockchainEntry = this.blockchainEntriesQueue.poll()) != null) {
                    this.createAddressProof(blockchainEntry, proof, privateKeyDatabase);
                }

                executorService.shutdownNow();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
                proof.getConnection().setAutoCommit(true);
                return proof;
            } finally {
                privateKeyDatabase.close();
                blockchain.close();
            }
        } catch (SQLException | InterruptedException e) {
            LOGGER.error("Exception while running asset proof", e);
            throw new IllegalArgumentException("Couldn't complete proof");
        }
    }

    private void createAddressProof(BlockchainEntry blockchainEntry, AssetProof proof, PrivateKeyDatabase privateKeyDatabase) {
        BigInteger balanceRandomness = ProofUtils.randomNumber();
        BigInteger keyKnownRandomness = ProofUtils.randomNumber();
        ECPoint pubKey = blockchainEntry.getPubKey();
        Optional<BigInteger> privateKey = privateKeyDatabase.retrievePrivateKey(pubKey);
        AddressProofData addressProofData = new AddressProofData(privateKey, pubKey, blockchainEntry.getBalance(), balanceRandomness, keyKnownRandomness, ECConstants.G, ECConstants.H);
        AddressProof addressProof = this.addressProofSystem.createProof(addressProofData);

        proof.addAddressProof(pubKey, addressProof);
    }

    private class AddressProofWorkerThread
            implements Runnable {
        private AddressProofWorkerThread() {
        }

        @Override
        public void run() {
            try (AssetProof assetProof = new AssetProof()) {
                try (SQLPrivateKeyDatabase database = new SQLPrivateKeyDatabase()) {
                    while (!Thread.interrupted()) {
                        try {
                            BlockchainEntry entry = AssetProofSystem.this.blockchainEntriesQueue.take();
                            AssetProofSystem.this.createAddressProof(entry, assetProof, database);
                        } catch (InterruptedException e) {
                            // empty catch block
                            break;
                        }
                    }


                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}

