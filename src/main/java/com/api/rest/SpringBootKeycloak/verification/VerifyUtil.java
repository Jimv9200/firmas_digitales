package com.api.rest.SpringBootKeycloak.verification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;

public class VerifyUtil {

    public static boolean verifyHash(byte[] fileHash, byte[] signature, PublicKey publicKey) throws Exception {
        // Verificar la firma usando el hash del archivo y la clave pública
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);
        sign.update(fileHash);
        return sign.verify(signature);
    }

    public static String loadHashFromFile(String filePath) throws IOException {
        // Leer el hash del archivo, solo el valor de hash (sin el nombre)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Lee la primera línea
            if (line != null) {
                return line.substring(0, line.lastIndexOf(" "));
            }
            return null;
        }
    }
}
