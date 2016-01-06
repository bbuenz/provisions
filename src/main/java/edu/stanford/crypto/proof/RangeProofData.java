/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

public class RangeProofData
implements ProofData<BinaryRangeProof> {
    private final BigInteger secret;
    private final List<BigInteger> randomnes;

    public RangeProofData(BigInteger secret, List<BigInteger> randomnes) {
        if (secret.bitLength() > randomnes.size()) {
            throw new IllegalArgumentException("What are you trying to have me proof here?");
        }
        this.secret = secret;
        this.randomnes = randomnes;
    }

    public BigInteger getSecret() {
        return this.secret;
    }

    public List<BigInteger> getRandomnes() {
        return this.randomnes;
    }

    public BigInteger getTotalRandomness() {
        return IntStream.range(0, this.randomnes.size()).mapToObj(i -> this.randomnes.get(i).shiftLeft(i)).reduce(BigInteger.ZERO, BigInteger::add).mod(ECConstants.Q);
    }
}

