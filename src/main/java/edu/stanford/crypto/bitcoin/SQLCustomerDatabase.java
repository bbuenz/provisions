/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.bitcoin;

import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.balance.CustomerInfo;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class SQLCustomerDatabase
implements CustomerDatabase,
SQLDatabase {
    public static final String QUERY_BALANCE_SQL = "SELECT balance FROM customer_balance WHERE customer_id=?";
    public static final String LIST_CUSTOMERS_SQL = "SELECT customer_id,balance FROM customer_balance";
    public static final String INSERT_CUSTOMER_SQL = "INSERT INTO customer_balance VALUES (?,?)";
    public static final String TRUNCATE_TABLE_SQL = "TRUNCATE TABLE customer_balance,balance_proof_secrets";
    private final PreparedStatement queryBalance;
    private final PreparedStatement listCustomers;
    private final PreparedStatement insertCustomer;
    private final PreparedStatement truncateDB;
    private final Connection connection = Database.getConnection();

    public SQLCustomerDatabase() throws SQLException {
        this.queryBalance = this.connection.prepareStatement("SELECT balance FROM customer_balance WHERE customer_id=?");
        this.listCustomers = this.connection.prepareStatement("SELECT customer_id,balance FROM customer_balance");
        this.insertCustomer = this.connection.prepareStatement("INSERT INTO customer_balance VALUES (?,?)");
        this.truncateDB = this.connection.prepareStatement("TRUNCATE TABLE customer_balance,balance_proof_secrets");
    }

    @Override
    public BigInteger getBalance(String customerId) {
        try {
            this.queryBalance.setString(1, customerId);
            ResultSet resultSet = this.queryBalance.executeQuery();
            if (resultSet.next()) {
                long balance = resultSet.getLong(1);
                return BigInteger.valueOf(balance);
            }
            throw new AssertionError((Object)("Customer not found " + customerId));
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
    }

    @Override
    public void addBalance(String customerId, BigInteger balance) {
        try {
            PreparedStatement statement = this.insertCustomer;
            statement.setString(1, customerId);
            statement.setLong(2, balance.longValueExact());
            statement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Iterator<CustomerInfo> getCustomers() {
        try {
            this.connection.setAutoCommit(false);
            final ResultSet resultSet = this.listCustomers.executeQuery();
            resultSet.setFetchSize(1000);
            return new Iterator<CustomerInfo>(){

                @Override
                public boolean hasNext() {
                    try {
                        boolean hasNext = resultSet.next();
                        if (!hasNext) {
                            SQLCustomerDatabase.this.connection.setAutoCommit(true);
                        }
                        return hasNext;
                    }
                    catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                }

                @Override
                public CustomerInfo next() {
                    try {
                        String id = resultSet.getString(1);
                        long balance = resultSet.getLong(2);
                        return new CustomerInfo(id, BigInteger.valueOf(balance));
                    }
                    catch (SQLException e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError(e);
        }
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

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

}

