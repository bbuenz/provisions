/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public interface RangeProof
extends MemoryProof {
    BigInteger getRange();

    ECPoint getStatement();
}

