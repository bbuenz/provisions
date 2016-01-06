/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.proof.binary.BinaryProof;
import org.bouncycastle.math.ec.ECPoint;

public class AddressProofEntry {
    private final ECPoint publicKey;
    private final AddressProof addressProof;
    private final BinaryProof binaryProof;

    public AddressProofEntry(ECPoint publicKey, AddressProof addressProof, BinaryProof binaryProof) {
        this.publicKey = publicKey;
        this.addressProof = addressProof;
        this.binaryProof = binaryProof;
    }

    public ECPoint getPublicKey() {
        return this.publicKey;
    }

    public AddressProof getAddressProof() {
        return this.addressProof;
    }

    public BinaryProof getBinaryProof() {
        return this.binaryProof;
    }
}

