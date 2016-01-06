/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class BlockchainEntry {
    private final ECPoint pubKey;
    private final BigInteger balance;

    public BlockchainEntry(ECPoint pubKey, BigInteger balance) {
        this.pubKey = pubKey;
        this.balance = balance;
    }

    public ECPoint getPubKey() {
        return this.pubKey;
    }

    public BigInteger getBalance() {
        return this.balance;
    }
}

