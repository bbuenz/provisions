/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.balance;

import edu.stanford.crypto.proof.ProofData;

public class BalanceProofData
implements ProofData<BalanceProof> {
    private final int maxBits;

    public BalanceProofData(int maxBits) {
        this.maxBits = maxBits;
    }

    public int getMaxBits() {
        return this.maxBits;
    }
}

