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
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class AssetProofSystem
        implements ProofSystem<AssetProof, AssetProofData> {
    private final AddressProofSystem addressProofSystem = new AddressProofSystem();
    private final BlockingQueue<BlockchainEntry> blockchainEntriesQueue = new ArrayBlockingQueue<BlockchainEntry>(Runtime.getRuntime().availableProcessors() * 3);
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
            Throwable throwable = null;
            try {
                AssetProof proof;
                ExecutorService executorService;
                Iterator<BlockchainEntry> blockchainEntries = blockchain.getBlockchainEntries();
                proof = new AssetProof();
                int nThreads = Runtime.getRuntime().availableProcessors() - 1;
                executorService = Executors.newFixedThreadPool(nThreads);
                proof.getConnection().setAutoCommit(false);
                Stream.generate(AddressProofWorkerThread::new).limit(nThreads).forEach(executorService::submit);
                SQLPrivateKeyDatabase privateKeyDatabase2 = new SQLPrivateKeyDatabase();
                Throwable throwable2 = null;
                try {
                    BlockchainEntry blockchainEntry;
                    while (blockchainEntries.hasNext()) {
                        blockchainEntry = blockchainEntries.next();
                        if (this.blockchainEntriesQueue.offer(blockchainEntry)) continue;
                        this.createAddressProof(blockchainEntry, proof, privateKeyDatabase2);
                    }
                    while ((blockchainEntry = this.blockchainEntriesQueue.poll()) != null) {
                        this.createAddressProof(blockchainEntry, proof, privateKeyDatabase2);
                    }
                } catch (Throwable blockchainEntry) {
                    throwable2 = blockchainEntry;
                    throw blockchainEntry;
                } finally {
                    if (throwable2 != null) {
                        try {
                            privateKeyDatabase2.close();
                        } catch (Throwable blockchainEntry) {
                            throwable2.addSuppressed(blockchainEntry);
                        }
                    } else {
                        privateKeyDatabase2.close();
                    }
                }
                executorService.shutdownNow();
                executorService.awaitTermination(1, TimeUnit.MINUTES);
                proof.getConnection().setAutoCommit(true);
                return proof;
            } catch (Throwable blockchainEntries) {
                throwable = blockchainEntries;
                throw blockchainEntries;
            } finally {
                if (throwable != null) {
                    try {
                        blockchain.close();
                    } catch (Throwable var9_14) {
                        throwable.addSuppressed(var9_14);
                    }
                } else {
                    blockchain.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't complete proof");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Couldn't complete proof");
    }

    private void createAddressProof(BlockchainEntry blockchainEntry, AssetProof proof, PrivateKeyDatabase privateKeyDatabase) {
        BigInteger balanceRandomness = ProofUtils.randomNumber();
        BigInteger keyKnownRandomness = ProofUtils.randomNumber();
        ECPoint pubKey = blockchainEntry.getPubKey();
        Optional<BigInteger> privateKey = privateKeyDatabase.retrievePrivateKey(pubKey);
        AddressProofData addressProofData = new AddressProofData(privateKey, pubKey, blockchainEntry.getBalance(), balanceRandomness, keyKnownRandomness, ECConstants.G, ECConstants.H);
        AddressProof addressProof = this.addressProofSystem.createProof(addressProofData);
        BinaryProofData binaryProofData = new BinaryProofData(privateKey.isPresent(), ECConstants.G, ECConstants.H, keyKnownRandomness);
        BinaryProof binaryProof = this.binaryProofSystem.createProof(binaryProofData);
        proof.addAddressProof(pubKey, addressProof, binaryProof);
    }

    private class AddressProofWorkerThread
            implements Runnable {
        private AddressProofWorkerThread() {
        }

        @Override
        public void run() {
            try {
                AssetProof assetProof = new AssetProof();
                Throwable throwable = null;
                try {
                    SQLPrivateKeyDatabase database = new SQLPrivateKeyDatabase();
                    Throwable throwable2 = null;
                    try {
                        while (!Thread.interrupted()) {
                            try {
                                BlockchainEntry entry = (BlockchainEntry) AssetProofSystem.this.blockchainEntriesQueue.take();
                                AssetProofSystem.this.createAddressProof(entry, assetProof, database);
                            } catch (InterruptedException e) {
                                // empty catch block
                                break;
                            }
                        }
                    } catch (Throwable e) {
                        throwable2 = e;
                        throw e;
                    } finally {
                        if (database != null) {
                            if (throwable2 != null) {
                                try {
                                    database.close();
                                } catch (Throwable e) {
                                    throwable2.addSuppressed(e);
                                }
                            } else {
                                database.close();
                            }
                        }
                    }
                } catch (Throwable database) {
                    throwable = database;
                    throw database;
                } finally {
                    if (assetProof != null) {
                        if (throwable != null) {
                            try {
                                assetProof.close();
                            } catch (Throwable database) {
                                throwable.addSuppressed(database);
                            }
                        } else {
                            assetProof.close();
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

