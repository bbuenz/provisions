/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Optional;

public interface PrivateKeyDatabase {
    void store(ECPoint var1, BigInteger var2);

    Optional<BigInteger> retrievePrivateKey(ECPoint var1);
}

