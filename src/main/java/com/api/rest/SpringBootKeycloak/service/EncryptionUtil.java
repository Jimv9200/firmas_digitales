package com.api.rest.SpringBootKeycloak.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
@Service
public class EncryptionUtil {

    private static final int RSA_BLOCK_SIZE = 245; // Máximo tamaño para encriptar con RSA de 2048 bits
    private static final int RSA_OUTPUT_BLOCK_SIZE = 256; // Tamaño del bloque de salida para RSA de 2048 bits

    // Método para encriptar un archivo usando RSA en bloques
    public void encryptFile(File inputFile, File outputFile, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[RSA_BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // Encriptar cada bloque
                byte[] encryptedBlock = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(encryptedBlock);
            }
        }
    }

    // Método para desencriptar un archivo usando RSA en bloques
    public  void decryptFile(File inputFile, File outputFile, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);


        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[RSA_OUTPUT_BLOCK_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // Desencriptar cada bloque
                byte[] decryptedBlock = cipher.doFinal(buffer, 0, bytesRead);
                fos.write(decryptedBlock);
            }
        }
        System.out.println("Archivo descencriptado");
    }
}