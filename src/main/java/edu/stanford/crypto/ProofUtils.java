/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto;

import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ProofUtils {
    private static final ThreadLocal<MessageDigest> SHA256;
    private static final SecureRandom RNG;

    public static BigInteger computeChallenge(ECPoint ... points) {
        for (ECPoint point : points) {
            SHA256.get().update(point.getEncoded(false));
        }
        byte[] hash = SHA256.get().digest();
        return new BigInteger(hash).mod(ECConstants.CHALLENGE_Q);
    }

    public static BigInteger hash(String id, BigInteger salt) {
        SHA256.get().update(id.getBytes());
        SHA256.get().update(salt.toByteArray());
        return new BigInteger(SHA256.get().digest());
    }

    public static BigInteger randomNumber(int bits) {
        return new BigInteger(bits, RNG);
    }

    public static BigInteger randomNumber() {
        return ProofUtils.randomNumber(256);
    }

    static {
        RNG = new SecureRandom();
        SHA256 = ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("SHA-256");
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }
        );
    }
}

