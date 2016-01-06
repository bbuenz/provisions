/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.experiments;

import edu.stanford.crypto.ExperimentUtils;
import edu.stanford.crypto.bitcoin.SQLCustomerDatabase;
import edu.stanford.crypto.bitcoin.SQLCustomerSecretsDatabase;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.balance.BalanceProof;
import edu.stanford.crypto.proof.balance.BalanceProofData;
import edu.stanford.crypto.proof.balance.BalanceProofSystem;
import edu.stanford.crypto.proof.balance.CustomerSecrets;
import edu.stanford.crypto.verification.BalanceVerificationData;
import edu.stanford.crypto.verification.BalanceVerifier;
import edu.stanford.crypto.verification.ParticipationData;
import edu.stanford.crypto.verification.ParticipationVerifier;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class BalanceProofExperiment {
    public static void main(String[] args) throws IOException, SQLException {
        Path outputFile = Paths.get(args[0], new String[0]);
        OutputStream stream = Files.newOutputStream(outputFile, new OpenOption[0]);
        Throwable throwable = null;
        try {
            stream.write("Bits;Customers;Proof Time;Verify time;Participation verify;File size\n".getBytes());
            int[] numberOfCustomers = new int[]{100, 200000, 2000000};
            int[] numberOfBits = new int[]{51};
            BalanceProofSystem proofSystem = new BalanceProofSystem();
            BalanceVerifier verifier = new BalanceVerifier();
            ParticipationVerifier participationVerifier = new ParticipationVerifier();
            for (int customers : numberOfCustomers) {
                for (int bits : numberOfBits) {
                    long startClearing = System.currentTimeMillis();
                    System.out.println("Starting to clear proofs ");
                    Database.clearProofs();
                    long endClearing = System.currentTimeMillis();
                    System.out.println("Took " + (double)(endClearing - startClearing) / 1000.0 + "s");
                    ExperimentUtils.generateRandomCustomers(customers, bits);
                    long endCreating = System.currentTimeMillis();
                    System.out.println("Took " + (double)(endCreating - endClearing) / 1000.0 + "s to create customers");
                    BalanceProofData data = new BalanceProofData(bits);
                    long startProof = System.currentTimeMillis();
                    BalanceProof proof = proofSystem.createProof(data);
                    long endProof = System.currentTimeMillis();
                    System.out.println("Finished balance proof in " + (endProof - startProof) / 1000 + "s");
                    verifier.verify(proof, new BalanceVerificationData(bits));
                    long endVerifyFull = System.currentTimeMillis();
                    System.out.println("Finished verification  in " + (endVerifyFull - endProof) / 1000 + "s");
                    String customer = "C0";
                    SQLCustomerDatabase customerDatabase = new SQLCustomerDatabase();
                    Throwable throwable2 = null;
                    try {
                        SQLCustomerSecretsDatabase secretsDatabase = new SQLCustomerSecretsDatabase();
                        Throwable throwable3 = null;
                        try {
                            CustomerSecrets secrets = secretsDatabase.retrieve(customer);
                            BigInteger balance = customerDatabase.getBalance(customer);
                            ParticipationData participationData = new ParticipationData(customer, secrets, balance);
                            participationVerifier.verify(proof, participationData);
                        }
                        catch (Throwable secrets) {
                            throwable3 = secrets;
                            throw secrets;
                        }
                        finally {
                            if (secretsDatabase != null) {
                                if (throwable3 != null) {
                                    try {
                                        secretsDatabase.close();
                                    }
                                    catch (Throwable secrets) {
                                        throwable3.addSuppressed(secrets);
                                    }
                                } else {
                                    secretsDatabase.close();
                                }
                            }
                        }
                    }
                    catch (Throwable secretsDatabase) {
                        throwable2 = secretsDatabase;
                        throw secretsDatabase;
                    }
                    finally {
                        if (customerDatabase != null) {
                            if (throwable2 != null) {
                                try {
                                    customerDatabase.close();
                                }
                                catch (Throwable secretsDatabase) {
                                    throwable2.addSuppressed(secretsDatabase);
                                }
                            } else {
                                customerDatabase.close();
                            }
                        }
                    }
                    long endVerifyParticipation = System.currentTimeMillis();
                    stream.write(("" + bits + ";" + customers + ";" + (endProof - startProof) + ";" + (endVerifyFull - endProof) + ";" + (endVerifyParticipation - endVerifyFull) + ";" + proof.getSizeInfo() + "\n").getBytes());
                    stream.flush();
                    proof.close();
                    System.out.println("Connections closed");
                }
            }
        }
        catch (Throwable numberOfCustomers) {
            throwable = numberOfCustomers;
            throw numberOfCustomers;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable numberOfCustomers) {
                        throwable.addSuppressed(numberOfCustomers);
                    }
                } else {
                    stream.close();
                }
            }
        }
        System.out.println("Proof Sucess");
    }
}

