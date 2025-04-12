package com.api.rest.SpringBootKeycloak.hash;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    // Método para convertir bytes a formato hexadecimal
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Método para generar el hash de un archivo
    public static byte[] generateHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] byteArray = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead;
            while ((bytesRead = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesRead);
            }
        }
        return digest.digest();  // Retorna el hash del archivo en formato de bytes
    }

    // Guardar el hash en un archivo en formato hexadecimal con el nombre del archivo
    public static void saveHashToFile(String hexHash, String fileName, String user) throws IOException {
        // Usamos FileWriter junto con BufferedWriter y OutputStreamWriter para asegurarnos de que usamos UTF-8
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream("src/main/webapp/usuario/"+user+"/signed/"+fileName+"SHA256.txt", true), StandardCharsets.UTF_8))) {
            // Guardamos el hash y el nombre del archivo en el archivo SHA256.txt
            String tamano= hexHash + " *" + fileName;
            System.out.println(tamano.length());
            writer.write(hexHash + " *" + fileName);
            writer.newLine();  // Añadimos un salto de línea al final
        }
    }

    // Método que convierte un código hash en formato hexadecimal a un array de bytes
    public static byte[] hexToBytes(String hex) {
        System.out.println("hash: " + hex);
        // Verifica que la longitud del hexadecimal sea par
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("El código hexadecimal debe tener una longitud par.");
        }

        byte[] bytes = new byte[hex.length() / 2];

        // Recorre el código hexadecimal de dos en dos caracteres
        for (int i = 0; i < hex.length(); i += 2) {
            int byteValue = Integer.parseInt(hex.substring(i, i + 2), 16);  // Convierte cada par de caracteres hexadecimales a un byte
            bytes[i / 2] = (byte) byteValue;
        }

        return bytes;
    }
}
