package com.api.rest.SpringBootKeycloak.firmado;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;

public class SignUtil {

    public static byte[] signHash(byte[] hash, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }

    public static void saveSignatureToFile(byte[] signature, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(signature);
        }
    }
}
