/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.binary.BinaryProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class BinaryProofVerifier
implements Verifier<BinaryProof, GeneratorData<BinaryProof>> {
    @Override
    public void verify(BinaryProof proof, GeneratorData<BinaryProof> data) {
        ECPoint zeroClaim = proof.getStatement().multiply(proof.getChallengeZero());
        ECPoint a0 = data.getH().multiply(proof.getResponseZero()).subtract(zeroClaim);
        ECPoint oneClaim = proof.getStatement().subtract(data.getG()).multiply(proof.getChallengeOne());
        ECPoint a1 = data.getH().multiply(proof.getResponseOne()).subtract(oneClaim);
        BigInteger computedChallenge = ProofUtils.computeChallenge(data.getG(), data.getH(), proof.getStatement(), a0, a1);
        BigInteger transmittedChallenge = proof.getChallengeZero().add(proof.getChallengeOne()).mod(ECConstants.CHALLENGE_Q);
        this.holds(transmittedChallenge, computedChallenge);
    }
}

