/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof;

public interface ProofSystem<P extends Proof, T extends ProofData<P>> {
    P createProof(T var1);
}

