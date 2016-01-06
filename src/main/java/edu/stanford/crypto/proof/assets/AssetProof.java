/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.proof.assets;

import edu.stanford.crypto.ECConstants;
import edu.stanford.crypto.SQLDatabase;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.Proof;
import edu.stanford.crypto.proof.binary.BinaryProof;
import org.bouncycastle.math.ec.ECPoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class AssetProof
implements Proof,
SQLDatabase {
    public static final String ADD_ADDRESS = "INSERT INTO asset_proof VALUES  (?, ?, ?) ";
    public static final String GET_PROOF_SQL = "SELECT asset_proof.main_proof,asset_proof.binary_proof FROM asset_proof WHERE public_key= ?";
    public static final String LIST_PROOFS_SQL = "SELECT asset_proof.public_key,asset_proof.main_proof,asset_proof.binary_proof FROM asset_proof ";
    public static final String READ_SIZE = "SELECT pg_size_pretty(pg_total_relation_size('public.asset_proof'))";
    private final PreparedStatement updateStatement;
    private final PreparedStatement readStatement;
    private final Connection connection = Database.getConnection();

    public AssetProof() throws SQLException {
        this.updateStatement = this.connection.prepareStatement("INSERT INTO asset_proof VALUES  (?, ?, ?) ");
        this.readStatement = this.connection.prepareStatement("SELECT asset_proof.main_proof,asset_proof.binary_proof FROM asset_proof WHERE public_key= ?");
    }

    @Override
    public void close() throws SQLException {
        this.connection.close();
    }

    public void addAddressProof(ECPoint publicKey, AddressProof proof, BinaryProof binaryProof) {
        try {
            this.updateStatement.setBytes(1, publicKey.getEncoded(true));
            this.updateStatement.setBytes(2, proof.serialize());
            this.updateStatement.setBytes(3, binaryProof.serialize());
            this.updateStatement.executeUpdate();
        }
        catch (SQLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public AddressProof getAddressProof(ECPoint publicKey) {
        try {
            this.readStatement.setBytes(1, publicKey.getEncoded(true));
            ResultSet resultSet = this.readStatement.executeQuery();
            if (resultSet.next()) {
                byte[] proof = resultSet.getBytes(1);
                return new AddressProof(proof);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new IllegalArgumentException("No such id " + publicKey.normalize());
    }

    public Iterator<AddressProofEntry> getAddressProofs() {
        try {
            this.connection.setAutoCommit(false);
            final ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT asset_proof.public_key,asset_proof.main_proof,asset_proof.binary_proof FROM asset_proof ");
            resultSet.setFetchSize(1000);
            return new Iterator<AddressProofEntry>(){

                @Override
                public boolean hasNext() {
                    try {
                        boolean next = resultSet.next();
                        if (!next) {
                            AssetProof.this.connection.setAutoCommit(true);
                        }
                        return next;
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        throw new IllegalStateException(e);
                    }
                }

                @Override
                public AddressProofEntry next() {
                    try {
                        byte[] publicKeyBytes = resultSet.getBytes(1);
                        byte[] proofBytes = resultSet.getBytes(2);
                        byte[] binaryProofBytes = resultSet.getBytes(3);
                        ECPoint publicKey = ECConstants.BITCOIN_CURVE.decodePoint(publicKeyBytes);
                        AddressProof addressProof = new AddressProof(proofBytes);
                        BinaryProof binaryProof = new BinaryProof(binaryProofBytes);
                        return new AddressProofEntry(publicKey, addressProof, binaryProof);
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

    public Connection getConnection() {
        return this.connection;
    }

    public String getSizeInfo() {
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery("SELECT pg_size_pretty(pg_total_relation_size('public.asset_proof'))");
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Couldn't run query SELECT pg_size_pretty(pg_total_relation_size('public.asset_proof'))");
    }

}

