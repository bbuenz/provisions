/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.proof.MemoryProof;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class AddressProof
        implements MemoryProof {
    private final ECPoint commitmentBalance;
    private final ECPoint commitmentXHat;
    private final BigInteger challengeZero;
    private final BigInteger challengeOne;

    private final BigInteger responseS;
    private final BigInteger responseV;
    private final BigInteger responseT;
    private final BigInteger responseXHat;
    private final BigInteger responseZero;
    private final BigInteger responseOne;

    public AddressProof(ECPoint commitmentBalance, ECPoint commitmentXHat,  BigInteger challengeZero, BigInteger challengeOne, BigInteger responseS, BigInteger responseV, BigInteger responseT, BigInteger responseXHat, BigInteger responseZero, BigInteger responseOne) {
        this.commitmentBalance = commitmentBalance;
        this.commitmentXHat = commitmentXHat;
        this.challengeZero = challengeZero;
        this.challengeOne = challengeOne;
        this.responseS = responseS;
        this.responseV = responseV;
        this.responseT = responseT;
        this.responseXHat = responseXHat;
        this.responseZero = responseZero;
        this.responseOne = responseOne;
    }

    public AddressProof(byte[] array) {
        int index = 0;
        byte statementLength = array[index++];
        this.commitmentBalance = ECConstants.BITCOIN_CURVE.decodePoint(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.commitmentXHat = ECConstants.BITCOIN_CURVE.decodePoint(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.challengeZero = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.challengeOne = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseS = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseT = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseV = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseXHat = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseZero = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
        index += statementLength;
        statementLength = array[index++];
        this.responseOne = new BigInteger(Arrays.copyOfRange(array, index, statementLength + index));
    }

    @Override
    public byte[] serialize() {
        byte[] commitmentBalanceEncoded = this.commitmentBalance.getEncoded(true);
        byte[] commitmentXHatEncoded = this.commitmentXHat.getEncoded(true);
        byte[] challengeZeroEncoded = this.challengeZero.toByteArray();
        byte[] challengeOneEncoded = this.challengeOne.toByteArray();

        byte[] responseSEncoded = this.responseS.toByteArray();
        byte[] responseTEncoded = this.responseT.toByteArray();
        byte[] responseVEncoded = this.responseV.toByteArray();
        byte[] responseXHatEncoded = this.responseXHat.toByteArray();
        byte[] responseZeroEncoded = this.responseZero.toByteArray();
        byte[] responseOneEncoded = this.responseOne.toByteArray();

        List<byte[]> arrList = Arrays.asList(commitmentBalanceEncoded, commitmentXHatEncoded, challengeZeroEncoded, challengeOneEncoded,responseSEncoded, responseTEncoded, responseVEncoded, responseXHatEncoded, responseZeroEncoded, responseOneEncoded);
        int totalLength = arrList.stream().mapToInt(arr -> arr.length).map(i -> i + 1).sum();
        byte[] fullArray = new byte[totalLength];
        int currIndex = 0;
        for (byte[] arr2 : arrList) {
            fullArray[currIndex++] = (byte) arr2.length;
            System.arraycopy(arr2, 0, fullArray, currIndex, arr2.length);
            currIndex += arr2.length;
        }
        return fullArray;
    }

    public BigInteger getChallengeZero() {
        return this.challengeZero;
    }

    public BigInteger getChallengeOne() {
        return challengeOne;
    }

    public BigInteger getResponseS() {
        return this.responseS;
    }

    public ECPoint getCommitmentBalance() {
        return this.commitmentBalance;
    }

    public ECPoint getCommitmentXHat() {
        return this.commitmentXHat;
    }

    public BigInteger getResponseV() {
        return this.responseV;
    }

    public BigInteger getResponseT() {
        return this.responseT;
    }

    public BigInteger getResponseXHat() {
        return this.responseXHat;
    }

    public BigInteger getResponseZero() {
        return responseZero;
    }

    public BigInteger getResponseOne() {
        return responseOne;
    }
}

