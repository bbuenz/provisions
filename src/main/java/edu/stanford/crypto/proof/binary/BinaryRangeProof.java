/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.binary;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.RangeProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryRangeProof
        implements RangeProof {
    private final List<BinaryProof> binaryProofList;

    @Override
    public byte[] serialize() {
        List<byte[]> bitSerializations = this.binaryProofList.stream().map(BinaryProof::serialize).collect(Collectors.toList());
        byte[] serialization = new byte[bitSerializations.stream().mapToInt(arr -> arr.length).sum()];
        int index = 0;
        for (byte[] arr2 : bitSerializations) {
            System.arraycopy(arr2, 0, serialization, index, arr2.length);
            index += arr2.length;
        }
        return serialization;
    }

    public BinaryRangeProof(List<BinaryProof> binaryProofList) {
        this.binaryProofList = binaryProofList;
    }

    public BinaryRangeProof(byte[] serialization) {
        int[] index = new int[]{0};
        ArrayList<BinaryProof> bits = new ArrayList<>();
        while (index[0] < serialization.length) {
            bits.add(new BinaryProof(serialization, index));
        }
        this.binaryProofList = bits;
    }

    @Override
    public BigInteger getRange() {
        return BigInteger.ONE.shiftLeft(this.binaryProofList.size()).subtract(BigInteger.ONE);
    }

    public List<BinaryProof> getBitProofs() {
        return this.binaryProofList;
    }

    @Override
    public ECPoint getStatement() {
        ECPoint statement = ECConstants.INFINITY;
        for (int i = 0; i < this.binaryProofList.size(); ++i) {
            statement = statement.add(this.binaryProofList.get(i).getStatement().timesPow2(i));
        }
        return statement;
    }
}

