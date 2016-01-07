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
            try {
                database.truncate();
            } finally {

                database.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        semaphore.release();
                    }
            ).fork();
        }
        try {
            semaphore.acquire(availableProcessors);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generatePublicKeys(int numAddresses) throws SQLException {
        SQLBlockchain blockchain = new SQLBlockchain();
        try {
            blockchain.truncate();
        } finally {

            blockchain.close();
        }
        Random rng = new Random();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        Semaphore semaphore = new Semaphore(0);
        int process = 0;
        while (process < availableProcessors) {
            SQLBlockchain processBlockchain = new SQLBlockchain();
            SQLPrivateKeyDatabase privateKeyDatabase = new SQLPrivateKeyDatabase();
            int processId = process++;
            ForkJoinTask.adapt(() -> {
                        try {
                            for (int i = processId; i < numAddresses; i += availableProcessors) {
                                BigInteger privateKey = new BigInteger(256, rng);
                                ECPoint publicKey = ECConstants.G.multiply(privateKey);
                                BigInteger balance = new BigInteger(10, rng);
                                processBlockchain.addEntry(publicKey, balance);
                                if (rng.nextDouble() >= 0.05) continue;
                                privateKeyDatabase.store(publicKey, privateKey);
                            }
                        } finally {
                            try {
                                privateKeyDatabase.close();
                                processBlockchain.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            semaphore.release();
                        }
                    }
            ).fork();
        }
        try {
            semaphore.acquire(availableProcessors);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

