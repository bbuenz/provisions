/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.binary;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.ProofSystem;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class BinaryProofSystem
implements ProofSystem<BinaryProof, BinaryProofData> {
    @Override
    public BinaryProof createProof(BinaryProofData data) {
        BinaryProof proof;
        BigInteger falseChallenge = ProofUtils.randomNumber(256).mod(ECConstants.CHALLENGE_Q);
        BigInteger u0 = ProofUtils.randomNumber();
        BigInteger u1 = ProofUtils.randomNumber();
        ECPoint h = data.getH();
        ECPoint a0 = h.multiply(u0);
        ECPoint a1 = h.multiply(u1);
        BigInteger randomness = data.getRandomness();
        ECPoint statement = h.multiply(randomness);
        ECPoint g = data.getG();
        if (data.getSecret()) {
            statement = statement.add(g);
            a0 = a0.add(g.multiply(falseChallenge.negate()));
        } else {
            a1 = a1.add(g.multiply(falseChallenge));
        }
        BigInteger challenge = ProofUtils.computeChallenge(g, h, statement, a0, a1);
        BigInteger trueChallenge = challenge.subtract(falseChallenge).mod(ECConstants.CHALLENGE_Q);
        if (data.getSecret()) {
            BigInteger responseZero = u0.add(falseChallenge.multiply(randomness)).mod(ECConstants.Q);
            BigInteger responseOne = u1.add(trueChallenge.multiply(randomness)).mod(ECConstants.Q);
            proof = new BinaryProof(statement, falseChallenge, trueChallenge, responseZero, responseOne);
        } else {
            BigInteger responseZero = u0.add(trueChallenge.multiply(randomness)).mod(ECConstants.Q);
            BigInteger responseOne = u1.add(falseChallenge.multiply(randomness)).mod(ECConstants.Q);
            proof = new BinaryProof(statement, trueChallenge, falseChallenge, responseZero, responseOne);
        }
        return proof;
    }
}

