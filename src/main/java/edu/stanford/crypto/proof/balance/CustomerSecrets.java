/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.balance;

import java.math.BigInteger;

public class CustomerSecrets {
    private final BigInteger hashSalt;
    private final BigInteger combinedRandomness;

    public CustomerSecrets(BigInteger hashSalt, BigInteger combinedRandomness) {
        this.hashSalt = hashSalt;
        this.combinedRandomness = combinedRandomness;
    }

    public BigInteger getHashSalt() {
        return this.hashSalt;
    }

    public BigInteger getCombinedRandomness() {
        return this.combinedRandomness;
    }
}

