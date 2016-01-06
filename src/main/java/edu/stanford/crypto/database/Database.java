/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.database;

import java.net.UnknownHostException;
import java.sql.*;

public final class Database {
    public static final String URL = "jdbc:postgresql://127.0.0.1/provisions";
    public static final String USER = "admin";
    public static final String PASSWORD = "password";

    private Database() throws UnknownHostException, ClassNotFoundException, SQLException {
    }

    public static Connection getConnection() throws SQLException {
        DriverManager.setLoginTimeout(10);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void clearProofs() {
        try {
            Connection connection = Database.getConnection();
            Throwable throwable = null;
            try {
                Statement statement = connection.createStatement();
                statement.execute("TRUNCATE TABLE balance_proof,balance_proof_secrets,asset_proof");
            }
            catch (Throwable statement) {
                throwable = statement;
                throw statement;
            }
            finally {
                if (connection != null) {
                    if (throwable != null) {
                        try {
                            connection.close();
                        }
                        catch (Throwable statement) {
                            throwable.addSuppressed(statement);
                        }
                    } else {
                        connection.close();
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void createTables() throws SQLException {
        Connection connection = Database.getConnection();
        String CREATE_BALANCE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS balance_proof (  customer_id_hash BYTEA NOT NULL,\n  range_proof BYTEA,\n  CONSTRAINT balance_proof_pkey PRIMARY KEY (customer_id_hash)\n)WITH (  OIDS=FALSE);ALTER TABLE balance_proof   OWNER TO USER";
        PreparedStatement createBalanceStatement = connection.prepareStatement(CREATE_BALANCE_TABLE_SQL);
        createBalanceStatement.execute();
        String CREATE_BALANCE_SECRETS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS balance_proof_secrets\n(\n  customer_id CHARACTER VARYING(9) NOT NULL,\n  hash_salt BYTEA,\n  balance_salt BYTEA,\n  CONSTRAINT balance_proof_secrets_pkey PRIMARY KEY (customer_id),\n  CONSTRAINT balance_proof_secrets_customer_id_fkey FOREIGN KEY (customer_id)\n      REFERENCES customer_balance (customer_id) MATCH SIMPLE\n      ON UPDATE CASCADE ON DELETE CASCADE\n)\nWITH (\n  OIDS=FALSE\n);\nALTER TABLE balance_proof_secrets\n  OWNER TO USER;";
        PreparedStatement createBalanceSecretsTable = connection.prepareStatement(CREATE_BALANCE_SECRETS_TABLE_SQL);
        createBalanceSecretsTable.execute();
        String CREATE_CUSTOMER_BALANCE = "CREATE TABLE IF NOT EXISTS customer_balance\n(\n  customer_id CHARACTER VARYING(9) NOT NULL,\n  balance BIGINT,\n  CONSTRAINT customer_balance_pkey PRIMARY KEY (customer_id)\n)\nWITH (\n  OIDS=FALSE\n);\nALTER TABLE customer_balance\n  OWNER TO USER;\n";
        PreparedStatement createCustomerBalance = connection.prepareStatement(CREATE_CUSTOMER_BALANCE);
        createCustomerBalance.execute();
        String CREATE_ASSETS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS assets_proof(hash_salt BYTEA,balance_salt BYTEA);";
        PreparedStatement createAssetsTable = connection.prepareStatement(CREATE_ASSETS_TABLE_SQL);
        createAssetsTable.execute();
        String CREATE_BLOCKCHAIN_SQL = "CREATE TABLE IF NOT EXISTS blockchain\n(\n  pubkey BYTEA NOT NULL,\n  balance BIGINT,\n  CONSTRAINT blockchain_pkey PRIMARY KEY (pubkey)\n)\nWITH (\n  OIDS=FALSE\n);\nALTER TABLE blockchain\n  OWNER TO USER;";
        PreparedStatement createBlockchain = connection.prepareStatement(CREATE_BLOCKCHAIN_SQL);
        createBlockchain.execute();
        String CREATE_ASSET_PROOF_SQL = "CREATE TABLE IF NOT EXISTS asset_proof\n(\n  public_key BYTEA NOT NULL,\n  main_proof BYTEA,\n  binary_proof BYTEA,\n  CONSTRAINT asset_proof_pkey PRIMARY KEY (public_key),\n  CONSTRAINT asset_proof_public_key_fkey FOREIGN KEY (public_key)\n      REFERENCES blockchain (public_key) MATCH SIMPLE\n      ON UPDATE NO ACTION ON DELETE NO ACTION\n)\nWITH (\n  OIDS=FALSE\n);\nALTER TABLE asset_proof\n  OWNER TO USER;\n";
        PreparedStatement createAssetProof = connection.prepareStatement(CREATE_ASSET_PROOF_SQL);
        createAssetProof.execute();
        String CREATE_ASSET_PROOF_SECRETS_SQL = "CREATE TABLE IF NOT EXISTS asset_proof_secrets \n(\n  public_key BYTEA NOT NULL,\n  private_key BYTEA,\n  CONSTRAINT asset_proof_secrets_pkey PRIMARY KEY (public_key),\n  CONSTRAINT asset_proof_secrets_public_key_fkey FOREIGN KEY (public_key)\n      REFERENCES blockchain (public_key) MATCH SIMPLE\n      ON UPDATE NO ACTION ON DELETE NO ACTION\n)\nWITH (\n  OIDS=FALSE\n);\nALTER TABLE asset_proof_secrets\n  OWNER TO USER;\n";
        PreparedStatement createAssetProofSecrets = connection.prepareStatement(CREATE_ASSET_PROOF_SECRETS_SQL);
        createAssetProofSecrets.execute();
        connection.close();
    }
}

