/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.proof.balance.BalanceProof;
import edu.stanford.crypto.proof.balance.CustomerSecrets;

import java.math.BigInteger;

public class ParticipationData
implements VerificationData<BalanceProof> {
    private final String customerId;
    private final CustomerSecrets secrets;
    private final BigInteger balance;

    public ParticipationData(String customerId, CustomerSecrets secrets, BigInteger balance) {
        this.customerId = customerId;
        this.secrets = secrets;
        this.balance = balance;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public CustomerSecrets getSecrets() {
        return this.secrets;
    }

    public BigInteger getBalance() {
        return this.balance;
    }
}

