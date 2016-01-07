/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.balance;

import com.sun.org.apache.regexp.internal.RE;
import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.Proof;
import edu.stanford.crypto.proof.binary.BinaryRangeProof;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

public class BalanceProof
        implements Proof,
        SQLDatabase {
    public static final String ADD_USER_SQL = "INSERT INTO balance_proof VALUES  (?, ?) ";
    public static final String GET_PROOF_SQL = "SELECT balance_proof.range_proof FROM balance_proof WHERE customer_id_hash= ?";
    public static final String LIST_PROOFS_SQL = "SELECT balance_proof.range_proof FROM balance_proof ";
    public static final String READ_SIZE = "SELECT pg_size_pretty(pg_total_relation_size('public.balance_proof'))";
    private final PreparedStatement updateStatement;
    private final PreparedStatement readStatement;
    private final Connection connection = Database.getConnection();

    public BalanceProof() throws SQLException {
        this.updateStatement = this.connection.prepareStatement(ADD_USER_SQL);
        this.readStatement = this.connection.prepareStatement(GET_PROOF_SQL);
    }

    public void addCustomer(BigInteger hashedId, BinaryRangeProof balanceProof) {
        try {
            this.updateStatement.setBytes(1, hashedId.toByteArray());
            this.updateStatement.setBytes(2, balanceProof.serialize());
            this.updateStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public BinaryRangeProof getCustomerProof(BigInteger hashedId) {
        try {
            this.readStatement.setBytes(1, hashedId.toByteArray());
            ResultSet resultSet = this.readStatement.executeQuery();
            if (resultSet.next()) {
                byte[] proof = resultSet.getBytes(1);
                return new BinaryRangeProof(proof);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new IllegalArgumentException("No such id " + Arrays.toString(hashedId.toByteArray()));
    }

    public Iterator<BinaryRangeProof> getRangeProofs() {
        try {
            this.connection.setAutoCommit(false);
            final ResultSet resultSet = this.connection.createStatement().executeQuery(LIST_PROOFS_SQL);
            resultSet.setFetchSize(1000);
            return new Iterator<BinaryRangeProof>() {

                @Override
                public boolean hasNext() {
                    try {
                        boolean next = resultSet.next();
                        if (!next) {
                            BalanceProof.this.connection.setAutoCommit(true);
                        }
                        return next;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }

                @Override
                public BinaryRangeProof next() {
                    try {
                        byte[] bytes = resultSet.getBytes(1);
                        return new BinaryRangeProof(bytes);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }
            };
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public String getSizeInfo() {
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery(READ_SIZE);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't run query " + READ_SIZE);
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

    public Connection getConnection() {
        return this.connection;
    }

}

