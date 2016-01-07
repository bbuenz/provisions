/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.proof.assets.AddressProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class AddressVerificationData
        implements VerificationData<AddressProof> {
    private final ECPoint publicKey;
    private final BigInteger balance;
    private final ECPoint g;
    private final ECPoint h;

    public AddressVerificationData(ECPoint publicKey, BigInteger balance, ECPoint g, ECPoint h) {
        this.publicKey = publicKey;
        this.balance = balance;
        this.g = g;
        this.h = h;
    }

    public ECPoint getPublicKey() {
        return this.publicKey;
    }

    public BigInteger getBalance() {
        return this.balance;
    }

    public ECPoint getG() {
        return this.g;
    }

    public ECPoint getH() {
        return this.h;
    }
}

