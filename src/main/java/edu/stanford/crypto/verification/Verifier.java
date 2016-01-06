/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.Proof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

interface Verifier<P extends Proof, T extends VerificationData<P>> {
    void verify(P var1, T var2);

    default void holds(ECPoint a, ECPoint b) {
        if (!a.equals(b)) {
            throw new AssertionError((Object)(a.normalize() + " should be equal to " + b.normalize()));
        }
    }

    default void holds(BigInteger a, BigInteger b) {
        if (!a.mod(ECConstants.Q).equals(b.mod(ECConstants.Q))) {
            throw new AssertionError((Object)(a + " should be equal to " + b));
        }
    }
}

