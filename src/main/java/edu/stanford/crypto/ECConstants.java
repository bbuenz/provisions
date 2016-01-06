/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto;

import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP256K1FieldElement;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class ECConstants {
    static {
        SecP256K1Curve curve = new SecP256K1Curve();
        BigInteger hash = BigInteger.ZERO;
        try {
            MessageDigest sha256 = sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update("PROVISIONS".getBytes());
            hash = new BigInteger(sha256.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        ECFieldElement x = new SecP256K1FieldElement(hash.mod(curve.getQ()));
        ECFieldElement rhs = x.square().multiply(x.add(curve.getA())).add(curve.getB());
        ECFieldElement y = rhs.sqrt();


        H = curve.validatePoint(x.toBigInteger(), y.toBigInteger());


    }

    public static final ECCurve BITCOIN_CURVE = new SecP256K1Curve();
    public static final ECPoint INFINITY = BITCOIN_CURVE.getInfinity();
    public static final int STANDARD_SECURITY = 256;
    public static final int CHALLENGE_LENGTH = 256;
    public static final ECPoint G = CustomNamedCurves.getByName("secp256k1").getG();
    public static final ECPoint H;
    public static final BigInteger Q = BITCOIN_CURVE.getOrder();
    public static final BigInteger CHALLENGE_Q = Q.min(BigInteger.ONE.shiftLeft(256));

    private ECConstants() {
    }
}

