package com.api.rest.SpringBootKeycloak.service;


import com.api.rest.SpringBootKeycloak.entities.PublicKeyEntity;
import com.api.rest.SpringBootKeycloak.entities.UserEntity;
import com.api.rest.SpringBootKeycloak.firmado.SignUtil;
import com.api.rest.SpringBootKeycloak.hash.HashUtil;
import com.api.rest.SpringBootKeycloak.keys.KeyGeneratorUtil;
import com.api.rest.SpringBootKeycloak.repositories.UserReposiroty;
import com.api.rest.SpringBootKeycloak.util.Util;
import com.api.rest.SpringBootKeycloak.verification.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class DigitalSignatureApp {

    @Autowired UserReposiroty userReposiroty;

    public Boolean DigitalSignatureApp(UserEntity user, String fileName, PrivateKey privateKey) throws Exception {

        File file = new File("src/main/webapp/usuario/"+user.getAlias()+"/uploads/"+fileName);

        // Verifica si el archivo SHA256.txt existe
        if (new File("src/main/webapp/usuario/"+user.getAlias()+"/signed/"+fileName+"SHA256.txt").exists()) {

            return verificarFirma(user,fileName,file);
        } else {
            System.out.println("No se encontraron archivos de firma. Realizando firma...");


            // Paso 2: Generar el hash del archivo
            byte[] fileHash = HashUtil.generateHash(file);

            // Convertir el hash a formato hexadecimal
            String hexHash = HashUtil.bytesToHex(fileHash);

            // Guardar el hash en el archivo SHA256.txt usando HashUtil
            HashUtil.saveHashToFile(hexHash, file.getName(), user.getAlias());

            // Imprimir el valor de hash generado en consola
            System.out.println("Hash del archivo original generado: " + hexHash);
            System.out.println("Hash del archivo generado y guardado en SHA256.txt");

            //Cargando llave privada
            //PrivateKey privateKey= KeyGeneratorUtil.loadPrivateKeyFromFile("src/main/webapp/usuario/"+user+"/keys/privateKey.key");

            // Paso 3: Firmar el hash del archivo
            byte[] signature = SignUtil.signHash(fileHash, privateKey);
            System.out.println("Hash del archivo firmado exitosamente.");

            // Guardar la firma en un archivo
            Files.write(new File("src/main/webapp/usuario/" + user.getAlias() + "/signed/" + fileName + "signature.sig").toPath(), signature);
            return (verificarFirma(user, fileName, file));

        }
    }

    public Boolean verificarFirma(UserEntity user, String fileName, File file) throws Exception {
        System.out.println("Archivos encontrados. Realizando verificación...");

        // Leer el hash desde el archivo SHA256.txt
        String rutaHash="src/main/webapp/usuario/"+user.getAlias()+"/signed/"+fileName+"SHA256.txt";
        String savedHash = VerifyUtil.loadHashFromFile(rutaHash);
        System.out.println("Hash del archivo original (verificación): " + savedHash);

        // Leer la firma desde el archivo para verificar
        byte[] signatureBytes = Files.readAllBytes(new File("src/main/webapp/usuario/"+user.getAlias()+"/signed/"+fileName+"signature.sig").toPath());

        // Cargar la clave pública desde el archivo
        //PublicKey publicKey = KeyGeneratorUtil.loadPublicKeyFromFile("src/main/webapp/usuario/"+user+"/keys/publicKey.key");
        System.out.println("voy a verificar publickey"+user);
        PublicKey publicKey= Util.convertStringToPublicKey(user.getPublicKey().getPublicKey());
        Util.printPublicKey(publicKey);
        System.out.println("Prueba hash nuevo:"+ HashUtil.bytesToHex(HashUtil.generateHash(file)));
        // Verificar la firma
        boolean isVerified = VerifyUtil.verifyHash(/*HashUtil.generateHash(file)*/HashUtil.hexToBytes(savedHash.split(" ")[0]), signatureBytes, publicKey);
        if(!isVerified) {
            Path rutaArchivo = Paths.get(rutaHash);
            System.out.println(rutaHash);
            Files.delete(rutaArchivo);
        }
        return isVerified;

    }

    public String crearLLaves(UserEntity user) throws Exception {
        try {
            // Paso 1: Generar el par de claves
            KeyPair keyPair = KeyGeneratorUtil.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            System.out.println(publicKey.toString()+"esta es la llave publica");

            System.out.println("voy a imprimir la llave");
            System.out.println(Util.printPublicKey(publicKey));
            PublicKeyEntity publicKey1= new PublicKeyEntity();
            publicKey1.setName(user.getAlias()+" privateKey");
            publicKey1.setPublicKey(Util.convertPublickeyToString(publicKey));
            publicKey1.setUser(user);

            user.setPublicKey(publicKey1);


            // Guardar las claves en archivos
            KeyGeneratorUtil.saveKeysToFile(publicKey, privateKey,user.getAlias());
        } catch (Exception e) {
            return "Error al crear las llaves"+e.getMessage();
        }
        return "Se crearon las llaves";
    }
}
