/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.verification;

import edu.stanford.crypto.BlockingExecutor;
import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.balance.BalanceProof;
import edu.stanford.crypto.proof.binary.BinaryProof;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BalanceVerifier
implements Verifier<BalanceProof, BalanceVerificationData> {
    private final BinaryRangeProofVerifier verifier = new BinaryRangeProofVerifier();

    @Override
    public void verify(BalanceProof proof, BalanceVerificationData data) {
        Iterator<BinaryRangeProof> rangeProofs = proof.getRangeProofs();
        RangeProofVerificationData verificationData = new RangeProofVerificationData(new GeneratorData<>(), BigInteger.ONE.shiftLeft(data.getMaxBits()).subtract(BigInteger.ONE));
        AtomicReference<ECPoint> totalEncryption = new AtomicReference<>(ECConstants.INFINITY);
        BlockingExecutor pool = new BlockingExecutor();
        while (rangeProofs.hasNext()) {
            BinaryRangeProof rangeProof = rangeProofs.next();
            Runnable thread = () -> {
                this.verifier.verify(rangeProof, verificationData);
                totalEncryption.accumulateAndGet(rangeProof.getStatement(), ECPoint::add);
            };
            pool.submit(thread);
        }
        try {
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.DAYS);
            System.out.println(totalEncryption.get());
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }
}

