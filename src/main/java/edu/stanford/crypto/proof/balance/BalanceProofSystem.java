/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.balance;

import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.bitcoin.CustomerSecretsDatabase;
import edu.stanford.crypto.bitcoin.SQLCustomerDatabase;
import edu.stanford.crypto.bitcoin.SQLCustomerSecretsDatabase;
import edu.stanford.crypto.proof.ProofSystem;
import edu.stanford.crypto.proof.RangeProofData;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;
import edu.stanford.crypto.proof.rangeproof.BinaryRangeProofSystem;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BalanceProofSystem
        implements ProofSystem<BalanceProof, BalanceProofData> {
    private final BinaryRangeProofSystem rangeProofSystem = new BinaryRangeProofSystem();
    private final BlockingQueue<CustomerInfo> customerInfos = new ArrayBlockingQueue<CustomerInfo>(Runtime.getRuntime().availableProcessors() * 3);
    private final AtomicReference<BigInteger> balanceKey = new AtomicReference<BigInteger>(BigInteger.ZERO);

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public BalanceProof createProof(BalanceProofData data) {
        try (SQLCustomerDatabase customerSecretsDatabase = new SQLCustomerDatabase()) {
            BalanceProof proof;
            ExecutorService executorService;
            Iterator<CustomerInfo> customerInfoIterator = customerSecretsDatabase.getCustomers();
            proof = new BalanceProof();
            int nThreads = Runtime.getRuntime().availableProcessors() - 1;
            executorService = Executors.newFixedThreadPool(nThreads);
            proof.getConnection().setAutoCommit(false);
            Stream.generate(() -> new RangeProofWorkerThread(data.getMaxBits())).limit(nThreads).forEach(executorService::submit);
            try (SQLCustomerSecretsDatabase database2 = new SQLCustomerSecretsDatabase()) {
                CustomerInfo info;
                while (customerInfoIterator.hasNext()) {
                    CustomerInfo customerInfo = customerInfoIterator.next();
                    if (this.customerInfos.offer(customerInfo)) continue;
                    this.createRangeProof(customerInfo, proof, database2, data.getMaxBits());
                }
                while ((info = this.customerInfos.poll()) != null) {
                    this.createRangeProof(info, proof, database2, data.getMaxBits());
                }
            }
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
            proof.getConnection().setAutoCommit(true);
            return proof;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Couldn't complete proof");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Couldn't complete proof");
    }

    public BigInteger getBalanceKey() {
        return this.balanceKey.get();
    }

    private void createRangeProof(CustomerInfo customerInfo, BalanceProof proof, CustomerSecretsDatabase database, int maxBits) {
        List<BigInteger> randomness = Stream.generate(ProofUtils::randomNumber).limit(maxBits).collect(Collectors.toList());
        BigInteger totalRandomness = BigInteger.ZERO;
        for (int i = 0; i < randomness.size(); ++i) {
            totalRandomness = totalRandomness.add(randomness.get(i).shiftLeft(i));
        }
        this.balanceKey.accumulateAndGet(totalRandomness, BigInteger::add);
        RangeProofData rangeProofData = new RangeProofData(customerInfo.getBalance(), randomness);
        BinaryRangeProof rangeProof = this.rangeProofSystem.createProof(rangeProofData);
        BigInteger hashSalt = ProofUtils.randomNumber();
        BigInteger hashedId = ProofUtils.hash(customerInfo.getId(), hashSalt);
        proof.addCustomer(hashedId, rangeProof);
        database.store(customerInfo.getId(), hashSalt, rangeProofData.getTotalRandomness());
    }

    private class RangeProofWorkerThread
            implements Runnable {
        private final int maxBits;

        private RangeProofWorkerThread(int maxBits) {
            this.maxBits = maxBits;
        }

        @Override
        public void run() {
            try (BalanceProof balanceProof = new BalanceProof()) {
                try (SQLCustomerSecretsDatabase database = new SQLCustomerSecretsDatabase()) {

                    while (!Thread.interrupted()) {
                        try {
                            CustomerInfo info = BalanceProofSystem.this.customerInfos.take();
                            BalanceProofSystem.this.createRangeProof(info, balanceProof, database, this.maxBits);
                        } catch (InterruptedException e) {
                            // empty catch block
                            break;
                        }
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

