/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.proof.binary.BinaryProof;
import org.bouncycastle.math.ec.ECPoint;

public class AddressProofEntry {
    private final ECPoint publicKey;
    private final AddressProof addressProof;

    public AddressProofEntry(ECPoint publicKey, AddressProof addressProof) {
        this.publicKey = publicKey;
        this.addressProof = addressProof;
    }

    public ECPoint getPublicKey() {
        return this.publicKey;
    }

    public AddressProof getAddressProof() {
        return this.addressProof;
    }

}

