/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SQLBlockchain
implements Blockchain,
SQLDatabase {
    private static final String INSERT_ENTRY_SQL = "INSERT INTO blockchain VALUES (?,?)";
    private static final String LIST_ENTRIES_SQL = "SELECT public_key,balance FROM blockchain";
    private static final String GET_BALANCE_SQL = "SELECT balance FROM blockchain WHERE public_key=?";
    public static final String TRUNCATE_TABLE_SQL = "TRUNCATE TABLE asset_proof,asset_proof_secrets,blockchain";
    private final Connection connection = Database.getConnection();
    private final PreparedStatement getBalance;
    private final PreparedStatement insertEntry;
    private final PreparedStatement truncateDB;

    public SQLBlockchain() throws SQLException {
        getBalance = this.connection.prepareStatement("SELECT balance FROM blockchain WHERE public_key=?");
        insertEntry = this.connection.prepareStatement("INSERT INTO blockchain VALUES (?,?)");
        truncateDB = this.connection.prepareStatement("TRUNCATE TABLE asset_proof,asset_proof_secrets,blockchain");
    }

    @Override
    public BigInteger getBalance(ECPoint publicKey) {
        try {
            this.getBalance.setBytes(1, publicKey.getEncoded(true));
            ResultSet resultSet = this.getBalance.executeQuery();
            if (resultSet.next()) {
                long balance = resultSet.getLong(1);
                return BigInteger.valueOf(balance);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Can't find " + publicKey);
    }

    @Override
    public void addEntry(ECPoint pubKey, BigInteger balance) {
        try {
            this.insertEntry.setBytes(1, pubKey.getEncoded(true));
            this.insertEntry.setLong(2, balance.longValueExact());
            this.insertEntry.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<BlockchainEntry> getBlockchainEntries() {
        try {
            final ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT public_key,balance FROM blockchain");
            return new Iterator<BlockchainEntry>(){

                @Override
                public boolean hasNext() {
                    try {
                        return resultSet.next();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }

                @Override
                public BlockchainEntry next() {
                    try {
                        byte[] pubKey = resultSet.getBytes(1);
                        long balance = resultSet.getLong(2);
                        return new BlockchainEntry(ECConstants.BITCOIN_CURVE.decodePoint(pubKey), BigInteger.valueOf(balance));
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }
            };
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

    @Override
    public void truncate() {
        try {
            this.truncateDB.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

