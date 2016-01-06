/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.balance;

import java.math.BigInteger;

public class CustomerInfo {
    private final String id;
    private final BigInteger balance;

    public CustomerInfo(String id, BigInteger balance) {
        this.id = id;
        this.balance = balance;
    }

    public String getId() {
        return this.id;
    }

    public BigInteger getBalance() {
        return this.balance;
    }
}

