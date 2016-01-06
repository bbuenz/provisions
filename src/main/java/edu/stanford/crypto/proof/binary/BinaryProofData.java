/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.binary;

import edu.stanford.crypto.proof.ProofData;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class BinaryProofData
implements ProofData<BinaryProof> {
    private final boolean secret;
    private final ECPoint g;
    private final ECPoint h;
    private final BigInteger randomness;

    public BinaryProofData(boolean secret, ECPoint g, ECPoint h, BigInteger randomness) {
        this.secret = secret;
        this.g = g;
        this.h = h;
        this.randomness = randomness;
    }

    public boolean getSecret() {
        return this.secret;
    }

    public ECPoint getG() {
        return this.g;
    }

    public ECPoint getH() {
        return this.h;
    }

    public BigInteger getRandomness() {
        return this.randomness;
    }
}

