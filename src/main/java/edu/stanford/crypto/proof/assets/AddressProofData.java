/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.proof.ProofData;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Optional;

class AddressProofData implements ProofData<AddressProof> {
    private final ECPoint publicKey;
    private final BigInteger balance;
    private final Optional<BigInteger> privateKey;
    private final BigInteger balanceRandomness;
    private final BigInteger keyKnownRandomness;
    private final ECPoint g;
    private final ECPoint h;

    public AddressProofData(BigInteger privateKey, ECPoint publicKey, BigInteger balance, BigInteger balanceRandomness, BigInteger keyKnownRandomness, ECPoint g, ECPoint h) {
        this.privateKey = Optional.of(privateKey);
        this.balanceRandomness = balanceRandomness;
        this.keyKnownRandomness = keyKnownRandomness;
        this.publicKey = publicKey;
        this.balance = balance;
        this.g = g;
        this.h = h;
    }

    public AddressProofData(Optional<BigInteger> privateKey, ECPoint publicKey, BigInteger balance, BigInteger balanceRandomness, BigInteger keyKnownRandomness, ECPoint g, ECPoint h) {
        this.privateKey =privateKey;
        this.balanceRandomness = balanceRandomness;
        this.keyKnownRandomness = keyKnownRandomness;
        this.publicKey = publicKey;
        this.balance = balance;
        this.g = g;
        this.h = h;
    }



    public Optional<BigInteger> getPrivateKey() {
        return this.privateKey;
    }

    public BigInteger getBalanceRandomness() {
        return this.balanceRandomness;
    }

    public BigInteger getKeyKnownRandomness() {
        return this.keyKnownRandomness;
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

