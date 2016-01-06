/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.proof.balance.BalanceProof;

public class BalanceVerificationData
implements VerificationData<BalanceProof> {
    private final int maxBits;

    public BalanceVerificationData(int maxBits) {
        this.maxBits = maxBits;
    }

    public int getMaxBits() {
        return this.maxBits;
    }
}

