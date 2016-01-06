/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.binary;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.MemoryProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class BinaryProof
implements MemoryProof {
    private final ECPoint statement;
    private final BigInteger challengeZero;
    private final BigInteger challengeOne;
    private final BigInteger responseZero;
    private final BigInteger responseOne;

    public BinaryProof(ECPoint statement, BigInteger challengeZero, BigInteger challengeOne, BigInteger responseZero, BigInteger responseOne) {
        this.statement = statement;
        this.challengeZero = challengeZero;
        this.challengeOne = challengeOne;
        this.responseZero = responseZero;
        this.responseOne = responseOne;
    }

    public BinaryProof(byte[] array) {
        this(array, new int[]{0});
    }

    public BinaryProof(byte[] array, int[] index) {
        int n = index[0];
        index[0] = n + 1;
        byte statementLength = array[n];
        this.statement = ECConstants.BITCOIN_CURVE.decodePoint(Arrays.copyOfRange(array, index[0], statementLength + index[0]));
        index[0] = index[0] + statementLength;
        int n2 = index[0];
        index[0] = n2 + 1;
        statementLength = array[n2];
        this.challengeZero = new BigInteger(Arrays.copyOfRange(array, index[0], statementLength + index[0]));
        index[0] = index[0] + statementLength;
        int n3 = index[0];
        index[0] = n3 + 1;
        statementLength = array[n3];
        this.challengeOne = new BigInteger(Arrays.copyOfRange(array, index[0], statementLength + index[0]));
        index[0] = index[0] + statementLength;
        int n4 = index[0];
        index[0] = n4 + 1;
        statementLength = array[n4];
        this.responseZero = new BigInteger(Arrays.copyOfRange(array, index[0], statementLength + index[0]));
        index[0] = index[0] + statementLength;
        int n5 = index[0];
        index[0] = n5 + 1;
        statementLength = array[n5];
        this.responseOne = new BigInteger(Arrays.copyOfRange(array, index[0], statementLength + index[0]));
        index[0] = index[0] + statementLength;
    }

    public ECPoint getStatement() {
        return this.statement;
    }

    public BigInteger getChallengeZero() {
        return this.challengeZero;
    }

    public BigInteger getChallengeOne() {
        return this.challengeOne;
    }

    public BigInteger getResponseZero() {
        return this.responseZero;
    }

    public BigInteger getResponseOne() {
        return this.responseOne;
    }

    @Override
    public byte[] serialize() {
        byte[] encodedStatement = this.statement.getEncoded(true);
        byte[] chall0Arr = this.challengeZero.toByteArray();
        byte[] challOneArr = this.challengeOne.toByteArray();
        byte[] responseZeroArr = this.responseZero.toByteArray();
        byte[] responseOneArr = this.responseOne.toByteArray();
        List<byte[]> arrList = Arrays.asList(encodedStatement, chall0Arr, challOneArr, responseZeroArr, responseOneArr);
        int totalLength = arrList.stream().mapToInt(arr -> arr.length).map(i -> i + 1).sum();
        byte[] fullArray = new byte[totalLength];
        int currIndex = 0;
        for (byte[] arr2 : arrList) {
            fullArray[currIndex++] = (byte)arr2.length;
            System.arraycopy(arr2, 0, fullArray, currIndex, arr2.length);
            currIndex += arr2.length;
        }
        return fullArray;
    }
}

