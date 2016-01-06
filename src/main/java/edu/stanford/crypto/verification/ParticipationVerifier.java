/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.ProofUtils;
import edu.stanford.crypto.proof.balance.BalanceProof;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ParticipationVerifier
implements Verifier<BalanceProof, ParticipationData> {
    @Override
    public void verify(BalanceProof proof, ParticipationData data) {
        BigInteger hashedId = ProofUtils.hash(data.getCustomerId(), data.getSecrets().getHashSalt());
        BinaryRangeProof customerProof = proof.getCustomerProof(hashedId);
        ECPoint assumedStatement = ECConstants.H.multiply(data.getSecrets().getCombinedRandomness()).add(ECConstants.G.multiply(data.getBalance()));
        this.holds(assumedStatement, customerProof.getStatement());
    }
}

