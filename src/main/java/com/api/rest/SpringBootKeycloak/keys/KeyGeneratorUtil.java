package com.api.rest.SpringBootKeycloak.keys;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
public class KeyGeneratorUtil {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        return keyPairGen.generateKeyPair();
    }

    public static void saveKeysToFile(PublicKey publicKey, PrivateKey privateKey, String user) throws IOException {
        //savePublicKey(publicKey, "src/main/webapp/usuario/"+user+"/keys/publicKey.key");
        savePrivateKey(privateKey, "src/main/webapp/usuario/"+user+"/keys/privateKey.key");
    }

    public static void savePublicKey(PublicKey publicKey, String filePath) throws IOException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(spec.getEncoded());
        }
    }

    public static void savePrivateKey(PrivateKey privateKey, String filePath) throws IOException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(spec.getEncoded());
        }
    }
    public static void decodificarLLave(PublicKey publicKey){


    }

    public static PublicKey loadPublicKeyFromFile(String filePath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        System.out.println("Se cargó la llave publica");
        return keyFactory.generatePublic(spec);
    }
    public static PrivateKey loadPrivateKeyFromFile(String filePath) throws Exception {
        // Leer los bytes de la clave privada desde el archivo
        byte[] keyBytes = Files.readAllBytes(Paths.get(filePath));

        // Crear la especificación de la clave privada (PKCS8)
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);

        // Obtener el KeyFactory para RSA y generar la clave privada
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        System.out.println("Se cargó la llave privada");
        return keyFactory.generatePrivate(spec);
    }

    public static PrivateKey loadPrivateKeyFromFile(MultipartFile filePath) throws Exception {
        // Leer los bytes de la clave privada desde el archivo
        //byte[] keyBytes = Files.readAllBytes((Path) filePath);

        // Crear la especificación de la clave privada (PKCS8)
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(filePath.getBytes());

        // Obtener el KeyFactory para RSA y generar la clave privada
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        System.out.println("Se cargó la llave privada");
        return keyFactory.generatePrivate(spec);
    }
}
