/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.proof.binary.BinaryProof;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;

import java.util.List;

public class BinaryRangeProofVerifier
implements Verifier<BinaryRangeProof, RangeProofVerificationData> {
    private final BinaryProofVerifier binaryProofVerifier = new BinaryProofVerifier();

    @Override
    public void verify(BinaryRangeProof proof, RangeProofVerificationData data) {
        if (data.getMaxRange().compareTo(proof.getRange()) > 0) {
            throw new AssertionError((Object)("Range is to small " + data.getMaxRange() + " should be less than " + proof.getRange()));
        }
        List<BinaryProof> bitProofs = proof.getBitProofs();
        for (int i = 0; i < bitProofs.size(); ++i) {
            this.binaryProofVerifier.verify(bitProofs.get(i), data.getGeneratorData());
        }
    }
}

