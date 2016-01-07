/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.ProofSystem;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Zero knowledge proof for public key y with balance b of the statement:<br>
 * "Either I know the private key x such that xG=y and p is a commitment to b or p is a commitment to 0."
 */
public class AddressProofSystem implements ProofSystem<AddressProof, AddressProofData> {
    @Override
    public AddressProof createProof(AddressProofData data) {
        BigInteger u1 = ProofUtils.randomNumber();
        BigInteger u2 = ProofUtils.randomNumber();
        BigInteger u3 = ProofUtils.randomNumber();
        BigInteger u4 = ProofUtils.randomNumber();
        ECPoint g = data.getG();
        ECPoint h = data.getH();
        ECPoint y = data.getPublicKey();
        ECPoint b = data.getG().multiply(data.getBalance());
        ECPoint a1 = b.multiply(u1).add(h.multiply(u2));
        ECPoint a2 = y.multiply(u1).add(h.multiply(u3));
        ECPoint a3 = g.multiply(u4).add(h.multiply(u3));
        Optional<BigInteger> privateKey = data.getPrivateKey();
        BigInteger v = data.getBalanceRandomness();
        BigInteger t = data.getKeyKnownRandomness();
        BigInteger s = privateKey.isPresent() ? BigInteger.ONE : BigInteger.ZERO;
        ECPoint p = b.multiply(s).add(h.multiply(v));
        ECPoint l = y.multiply(s).add(h.multiply(t));

        //Binary proof
        BigInteger uZero = ProofUtils.randomNumber();
        BigInteger uOne = ProofUtils.randomNumber();
        BigInteger falseChallenge = ProofUtils.randomNumber(256).mod(ECConstants.CHALLENGE_Q);
        ECPoint falseChallengeElement = b.multiply(falseChallenge);
        ECPoint aZero = h.multiply(uZero).subtract(falseChallengeElement.multiply(s));
        ECPoint aOne = h.multiply(uOne).add(falseChallengeElement.multiply(BigInteger.ONE.subtract(s)));


        BigInteger challenge = ProofUtils.computeChallenge(g, h, y, b, p, l, a1, a2, a3, aZero, aOne);
        BigInteger responseS = u1.add(challenge.multiply(s));
        BigInteger responseV = u2.add(challenge.multiply(v));
        BigInteger responseT = u3.add(challenge.multiply(t));
        BigInteger responseX = u4.add(challenge.multiply(privateKey.orElse(BigInteger.ZERO)));
        BigInteger trueChallenge = challenge.subtract(falseChallenge).mod(ECConstants.CHALLENGE_Q);
        BigInteger falseV = falseChallenge.multiply(v);
        BigInteger trueV = trueChallenge.multiply(v);

        BigInteger responseZero = uZero.add(trueV.multiply(BigInteger.ONE.subtract(s))).add(falseV.multiply(s)).mod(ECConstants.Q);
        BigInteger responseOne = uOne.add(falseV.multiply(BigInteger.ONE.subtract(s))).add(trueV.multiply(s)).mod(ECConstants.Q);


        return new AddressProof(p, l, privateKey.isPresent() ? falseChallenge : trueChallenge, privateKey.isPresent() ? trueChallenge : falseChallenge, responseS, responseV, responseT, responseX, responseZero, responseOne);
    }

}

