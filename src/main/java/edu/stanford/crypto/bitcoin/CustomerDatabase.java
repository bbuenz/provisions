/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.proof.balance.CustomerInfo;

import java.math.BigInteger;
import java.util.Iterator;

interface CustomerDatabase
extends AutoCloseable {
    BigInteger getBalance(String var1);

    void addBalance(String var1, BigInteger var2);

    Iterator<CustomerInfo> getCustomers();

    void truncate();
}

