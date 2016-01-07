/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.assets.AddressProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class AddressProofVerifier
        implements Verifier<AddressProof, AddressVerificationData> {
    @Override
    public void verify(AddressProof proof, AddressVerificationData data) {
        ECPoint g = data.getG();
        ECPoint h = data.getH();
        ECPoint b = g.multiply(data.getBalance());
        BigInteger transmittedChallenge = proof.getChallengeZero().add(proof.getChallengeOne());
        ECPoint balanceClaim = proof.getCommitmentBalance().multiply(transmittedChallenge);
        ECPoint a1 = h.multiply(proof.getResponseV()).add(b.multiply(proof.getResponseS())).subtract(balanceClaim);
        ECPoint xHatClaim = proof.getCommitmentXHat().multiply(transmittedChallenge);
        ECPoint a2 = data.getPublicKey().multiply(proof.getResponseS()).add(h.multiply(proof.getResponseT())).subtract(xHatClaim);
        ECPoint a3 = g.multiply(proof.getResponseXHat()).add(h.multiply(proof.getResponseT())).subtract(xHatClaim);
        ECPoint zeroClaim = proof.getCommitmentBalance().multiply(proof.getChallengeZero());
        ECPoint oneClaim = proof.getCommitmentBalance().subtract(b).multiply(proof.getChallengeOne());

        ECPoint aZero = h.multiply(proof.getResponseZero()).subtract(zeroClaim);
        ECPoint aOne = h.multiply(proof.getResponseOne()).subtract(oneClaim);

        BigInteger computedChallenge = ProofUtils.computeChallenge(g, h, data.getPublicKey(), b, proof.getCommitmentBalance(), proof.getCommitmentXHat(), a1, a2, a3, aZero, aOne);
        this.holds(transmittedChallenge, computedChallenge);
    }
}

