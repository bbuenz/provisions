package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.proof.RangeProofData;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;
import edu.stanford.crypto.proof.rangeproof.BinaryRangeProofSystem;
import edu.stanford.crypto.verification.BinaryRangeProofVerifier;
import edu.stanford.crypto.verification.GeneratorData;
import edu.stanford.crypto.verification.RangeProofVerificationData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by buenz on 07.01.16.
 */
public class RangeProofTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testRangeProof() {
        BinaryRangeProofSystem rangeProofSystem = new BinaryRangeProofSystem();
        BigInteger secretData = BigInteger.TEN;
        SecureRandom rng = new SecureRandom();
        List<BigInteger> hidingFactors = Stream.generate(() -> new BigInteger(256, rng)).limit(4).collect(Collectors.toList());
        RangeProofData data = new RangeProofData(secretData, hidingFactors);
        BinaryRangeProof proof = rangeProofSystem.createProof(data);
        BinaryRangeProofVerifier verifier=new BinaryRangeProofVerifier();

        RangeProofVerificationData rangeProofVerificationData=new RangeProofVerificationData(new GeneratorData<>(),BigInteger.valueOf(15));

        verifier.verify(proof,rangeProofVerificationData);

    }
    @Test
    public void testFailedRangeProof(){
        exception.expect(AssertionError.class);
        BinaryRangeProofSystem rangeProofSystem = new BinaryRangeProofSystem();
        BigInteger secretData = BigInteger.valueOf(2);
        SecureRandom rng = new SecureRandom();
        List<BigInteger> hidingFactors = Stream.generate(() -> new BigInteger(256, rng)).limit(3).collect(Collectors.toList());
        RangeProofData data = new RangeProofData(secretData, hidingFactors);
        BinaryRangeProof proof = rangeProofSystem.createProof(data);
        BinaryRangeProofVerifier verifier=new BinaryRangeProofVerifier();

        RangeProofVerificationData rangeProofVerificationData=new RangeProofVerificationData(new GeneratorData<>(),BigInteger.valueOf(15));

        verifier.verify(proof,rangeProofVerificationData);
    }
}
