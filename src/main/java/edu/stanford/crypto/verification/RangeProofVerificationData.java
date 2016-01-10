/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.proof.binary.BinaryProof;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;

import java.math.BigInteger;

public class RangeProofVerificationData implements VerificationData<BinaryRangeProof> {
    private final GeneratorData<BinaryProof> generatorData;
    private final BigInteger maxRange;

    public RangeProofVerificationData(GeneratorData<BinaryProof> generatorData, BigInteger maxRange) {
        this.generatorData = generatorData;
        this.maxRange = maxRange;
    }

    public GeneratorData<BinaryProof> getGeneratorData() {
        return this.generatorData;
    }

    public BigInteger getMaxRange() {
        return this.maxRange;
    }
}

