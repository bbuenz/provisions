package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.verification.AddressProofVerifier;
import edu.stanford.crypto.verification.AddressVerificationData;
import org.bouncycastle.math.ec.ECPoint;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by buenz on 06.01.16.
 */
public class AddressProofTest {
    @Test
    public void testAddressProofWithKey() {
        AddressProofSystem system = new AddressProofSystem();
        Random rng = new SecureRandom();

        BigInteger privateKey = new BigInteger(256, rng);
        ECPoint pubKey = ECConstants.G.multiply(privateKey);
        BigInteger balance = BigInteger.TEN;
        BigInteger balanceRandomness = new BigInteger(256, rng);
        BigInteger knownKeyRandomness = new BigInteger(256, rng);

        AddressProofData data = new AddressProofData(privateKey, pubKey, balance, balanceRandomness, knownKeyRandomness, ECConstants.G, ECConstants.H);
        AddressProof proof = system.createProof(data);
        AddressProofVerifier verifier = new AddressProofVerifier();
        AddressVerificationData verificaitonData = new AddressVerificationData(pubKey, balance, ECConstants.G, ECConstants.H);
        verifier.verify(proof, verificaitonData);
    }
}
