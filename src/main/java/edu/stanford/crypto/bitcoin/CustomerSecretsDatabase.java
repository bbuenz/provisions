/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.proof.balance.CustomerSecrets;

import java.math.BigInteger;

public interface CustomerSecretsDatabase {
    void store(String var1, BigInteger var2, BigInteger var3);

    CustomerSecrets retrieve(String var1);
}

