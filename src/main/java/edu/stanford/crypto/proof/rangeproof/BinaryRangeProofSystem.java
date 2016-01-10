/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.rangeproof;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.ProofSystem;
import edu.stanford.crypto.proof.RangeProofData;
import edu.stanford.crypto.proof.binary.BinaryProof;
import edu.stanford.crypto.proof.binary.BinaryProofData;
import edu.stanford.crypto.proof.binary.BinaryProofSystem;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;

import java.math.BigInteger;
import java.util.ArrayList;

public class BinaryRangeProofSystem
implements ProofSystem<BinaryRangeProof, RangeProofData> {
    private final BinaryProofSystem bitsProver = new BinaryProofSystem();

    @Override
    public BinaryRangeProof createProof(RangeProofData data) {
        BigInteger secret = data.getSecret();
        ArrayList<BinaryProof> binaryProofList = new ArrayList<>(data.getRandomnes().size());
        for (int i = 0; i < data.getRandomnes().size(); ++i) {
            BinaryProofData binaryProofData = new BinaryProofData(secret.testBit(i), ECConstants.G, ECConstants.H, data.getRandomnes().get(i));
            binaryProofList.add(this.bitsProver.createProof(binaryProofData));
        }
        return new BinaryRangeProof(binaryProofList);
    }
}

