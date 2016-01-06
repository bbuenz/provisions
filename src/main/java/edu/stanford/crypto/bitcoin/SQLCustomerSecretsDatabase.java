/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.balance.CustomerSecrets;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLCustomerSecretsDatabase
implements CustomerSecretsDatabase,
SQLDatabase {
    public static final String RETRIEVE_SECRETS_SQL = "SELECT hash_salt,balance_salt FROM balance_proof_secrets WHERE customer_id=?";
    public static final String INSERT_INTO_SQL = "INSERT INTO balance_proof_secrets VALUES (?,?,?)";
    private final PreparedStatement retrieveSecrets;
    private final PreparedStatement insertSecrets;
    private final Connection connection = Database.getConnection();

    public SQLCustomerSecretsDatabase() throws SQLException {
        this.retrieveSecrets = this.connection.prepareStatement("SELECT hash_salt,balance_salt FROM balance_proof_secrets WHERE customer_id=?");
        this.insertSecrets = this.connection.prepareStatement("INSERT INTO balance_proof_secrets VALUES (?,?,?)");
    }

    @Override
    public void store(String customerId, BigInteger hashSalt, BigInteger balanceSalt) {
        try {
            this.insertSecrets.setString(1, customerId);
            this.insertSecrets.setBytes(2, hashSalt.toByteArray());
            this.insertSecrets.setBytes(3, balanceSalt.toByteArray());
            this.insertSecrets.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CustomerSecrets retrieve(String customerId) {
        try {
            this.retrieveSecrets.setString(1, customerId);
            ResultSet resultSet = this.retrieveSecrets.executeQuery();
            if (resultSet.next()) {
                BigInteger hashSalt = new BigInteger(resultSet.getBytes(1));
                BigInteger balanceRandomness = new BigInteger(resultSet.getBytes(2));
                return new CustomerSecrets(hashSalt, balanceRandomness);
            }
            throw new IllegalArgumentException("Customer not found " + customerId);
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

