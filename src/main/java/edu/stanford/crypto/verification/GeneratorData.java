/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.Proof;
import org.bouncycastle.math.ec.ECPoint;

public class GeneratorData<P extends Proof>
implements VerificationData<P> {
    private final ECPoint g;
    private final ECPoint h;

    public GeneratorData() {
        this(ECConstants.G, ECConstants.H);
    }

    private GeneratorData(ECPoint g, ECPoint h) {
        this.g = g;
        this.h = h;
    }

    public ECPoint getG() {
        return this.g;
    }

    public ECPoint getH() {
        return this.h;
    }
}

