/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.ProofSystem;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Optional;

public class AddressProofSystem
implements ProofSystem<AddressProof, AddressProofData> {
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
        BigInteger challenge = ProofUtils.computeChallenge(g, h, y, b, p, l, a1, a2, a3);
        BigInteger responseS = u1.add(challenge.multiply(s));
        BigInteger responseV = u2.add(challenge.multiply(v));
        BigInteger responseT = u3.add(challenge.multiply(t));
        BigInteger responseX = u4.add(challenge.multiply(privateKey.orElse(BigInteger.ZERO)));
        return new AddressProof(p, l, challenge, responseS, responseV, responseT, responseX);
    }
}

