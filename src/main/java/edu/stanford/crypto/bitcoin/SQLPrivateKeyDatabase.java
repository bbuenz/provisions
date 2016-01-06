/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLPrivateKeyDatabase
implements PrivateKeyDatabase,
SQLDatabase {
    public static final String RETRIEVE_SECRETS_SQL = "SELECT private_key FROM asset_proof_secrets WHERE public_key=?";
    public static final String INSERT_INTO_SQL = "INSERT INTO asset_proof_secrets VALUES (?,?)";
    private final PreparedStatement retrieveSecrets;
    private final PreparedStatement insertSecrets;
    private final Connection connection = Database.getConnection();

    public SQLPrivateKeyDatabase() throws SQLException {
        this.retrieveSecrets = this.connection.prepareStatement("SELECT private_key FROM asset_proof_secrets WHERE public_key=?");
        this.insertSecrets = this.connection.prepareStatement("INSERT INTO asset_proof_secrets VALUES (?,?)");
    }

    @Override
    public void store(ECPoint publicKey, BigInteger privateKey) {
        try {
            this.insertSecrets.setBytes(1, publicKey.getEncoded(true));
            this.insertSecrets.setBytes(2, privateKey.toByteArray());
            this.insertSecrets.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<BigInteger> retrievePrivateKey(ECPoint publicKey) {
        try {
            this.retrieveSecrets.setBytes(1, publicKey.getEncoded(true));
            ResultSet resultSet = this.retrieveSecrets.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new BigInteger(resultSet.getBytes(1)));
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }
}

