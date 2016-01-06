/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto;

import edu.stanford.crypto.bitcoin.SQLBlockchain;
import edu.stanford.crypto.bitcoin.SQLCustomerDatabase;
import edu.stanford.crypto.bitcoin.SQLPrivateKeyDatabase;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Semaphore;

public class ExperimentUtils {
    public static void generateRandomCustomers(int numCustomers, int maxBits) throws SQLException {
        try {
            SQLCustomerDatabase database = new SQLCustomerDatabase();
            Throwable throwable = null;
            try {
                database.truncate();
            }
            catch (Throwable var4_7) {
                throwable = var4_7;
                throw var4_7;
            }
            finally {
                if (database != null) {
                    if (throwable != null) {
                        try {
                            database.close();
                        }
                        catch (Throwable var4_6) {
                            throwable.addSuppressed(var4_6);
                        }
                    } else {
                        database.close();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Random rng = new Random();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        Semaphore semaphore = new Semaphore(0);
        int process = 0;
        while (process < availableProcessors) {
            SQLCustomerDatabase processDb = new SQLCustomerDatabase();
            int processId = process++;
            ForkJoinTask.adapt(() -> {
                for (int i = processId; i < numCustomers; i += availableProcessors) {
                    String customerId = "C" + i;
                    BigInteger balance = rng.nextDouble() > 0.9 ? new BigInteger(maxBits, rng) : new BigInteger(10, rng);
                    processDb.addBalance(customerId, balance);
                }
                try {
                    processDb.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                semaphore.release();
            }
            ).fork();
        }
        try {
            semaphore.acquire(availableProcessors);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generatePublicKeys(int numAddresses) throws SQLException {
        SQLBlockchain blockchain = new SQLBlockchain();
        Throwable throwable = null;
        try {
            blockchain.truncate();
        }
        catch (Throwable var3_5) {
            throwable = var3_5;
            throw var3_5;
        }
        finally {
            if (blockchain != null) {
                if (throwable != null) {
                    try {
                        blockchain.close();
                    }
                    catch (Throwable var3_4) {
                        throwable.addSuppressed(var3_4);
                    }
                } else {
                    blockchain.close();
                }
            }
        }
        Random rng = new Random();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        Semaphore semaphore = new Semaphore(0);
        int process = 0;
        while (process < availableProcessors) {
            SQLBlockchain blockchain2 = new SQLBlockchain();
            SQLPrivateKeyDatabase privateKeyDatabase = new SQLPrivateKeyDatabase();
            int processId = process++;
            ForkJoinTask.adapt(() -> {
                try {
                    for (int i = processId; i < numAddresses; i += availableProcessors) {
                        BigInteger privateKey = new BigInteger(256, rng);
                        ECPoint publicKey = ECConstants.G.multiply(privateKey);
                        BigInteger balance = new BigInteger(10, rng);
                        blockchain2.addEntry(publicKey, balance);
                        if (rng.nextDouble() >= 0.05) continue;
                        privateKeyDatabase.store(publicKey, privateKey);
                    }
                }
                finally {
                    try {
                        privateKeyDatabase.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        blockchain2.close();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    semaphore.release();
                }
            }
            ).fork();
        }
        try {
            semaphore.acquire(availableProcessors);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

