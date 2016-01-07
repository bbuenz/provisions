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
            throw new AssertionError("Range is to small " + data.getMaxRange() + " should be less than " + proof.getRange());
        }
        GeneratorData<BinaryProof> generatorData = data.getGeneratorData();
        proof.getBitProofs().forEach(bp -> binaryProofVerifier.verify(bp, generatorData));
    }
}

