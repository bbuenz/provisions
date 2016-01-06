/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto.experiments;

import edu.stanford.crypto.ExperimentUtils;
import edu.stanford.crypto.database.Database;
import edu.stanford.crypto.proof.assets.AssetProof;
import edu.stanford.crypto.proof.assets.AssetProofSystem;
import edu.stanford.crypto.verification.AssetsVerifier;
import edu.stanford.crypto.verification.GeneratorData;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class AssetProofExperiment {
    public static void main(String[] args) throws IOException, SQLException {
        Path outputFile = Paths.get(args[0], new String[0]);
        OutputStream stream = Files.newOutputStream(outputFile, new OpenOption[0]);
        Throwable throwable = null;
        try {
            int[] numberOfAddresses;
            stream.write("Addresses;Proof Time;Verify time;File size\n".getBytes());
            for (int numKeys : numberOfAddresses = new int[]{10, 100, 1000, 10000, 100000, 200000, 500000}) {
                AssetProofSystem proofSystem = new AssetProofSystem();
                AssetsVerifier verifier = new AssetsVerifier();
                GeneratorData<AssetProof> data = new GeneratorData<AssetProof>();
                long startClearing = System.currentTimeMillis();
                System.out.println("Starting to clear proofs ");
                Database.clearProofs();
                long endClearing = System.currentTimeMillis();
                System.out.println("Took " + (double)(endClearing - startClearing) / 1000.0 + "s");
                ExperimentUtils.generatePublicKeys(numKeys);
                long endCreating = System.currentTimeMillis();
                System.out.println("Took " + (double)(endCreating - endClearing) / 1000.0 + "s to create keys");
                long startProof = System.currentTimeMillis();
                AssetProof proof = proofSystem.createProof(null);
                long endProof = System.currentTimeMillis();
                System.out.println("Finished balance proof in " + (endProof - startProof) / 1000 + "s");
                verifier.verify(proof, data);
                long endVerifyFull = System.currentTimeMillis();
                System.out.println("Finished verification  in " + (endVerifyFull - endProof) / 1000 + "s");
                stream.write(("" + numKeys + ";" + (endProof - startProof) + ";" + (endVerifyFull - endProof) + ";" + proof.getSizeInfo() + "\n").getBytes());
                stream.flush();
                proof.close();
                System.out.println("Connections closed");
            }
        }
        catch (Throwable numberOfAddresses) {
            throwable = numberOfAddresses;
            throw numberOfAddresses;
        }
        finally {
            if (stream != null) {
                if (throwable != null) {
                    try {
                        stream.close();
                    }
                    catch (Throwable numberOfAddresses) {
                        throwable.addSuppressed(numberOfAddresses);
                    }
                } else {
                    stream.close();
                }
            }
        }
        System.out.println("Proof Sucess");
    }
}

