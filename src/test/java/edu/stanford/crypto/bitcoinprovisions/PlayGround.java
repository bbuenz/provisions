package edu.stanford.crypto.bitcoinprovisions;

import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.bouncycastle.math.ec.custom.sec.SecP256K1FieldElement;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Point;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Unit test for simple App.
 */
public class PlayGround {

    @Test
    public void playGround2() throws IOException, NoSuchAlgorithmException {
        SecP256K1Curve curve = new SecP256K1Curve();
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

        sha256.update("Provisions".getBytes());
        BigInteger hash = new BigInteger(sha256.digest());

        ECFieldElement x = new SecP256K1FieldElement(hash.mod(curve.getQ()));
        ECFieldElement rhs = x.square().multiply(x.add(curve.getA())).add(curve.getB());
        ECFieldElement y = rhs.sqrt();
        ECFieldElement one=new SecP256K1FieldElement(BigInteger.ONE);
        while (y==null){
            x=x.add(one);
            y=x.square().multiply(x.add(curve.getA())).add(curve.getB()).sqrt();
        }

        ECPoint point = curve.validatePoint(x.toBigInteger(), y.toBigInteger());
        System.out.println(point);
    }

}
