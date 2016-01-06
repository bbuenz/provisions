/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Iterator;

interface Blockchain {
    BigInteger getBalance(ECPoint var1);

    void addEntry(ECPoint var1, BigInteger var2);

    Iterator<BlockchainEntry> getBlockchainEntries();

    void truncate();
}

