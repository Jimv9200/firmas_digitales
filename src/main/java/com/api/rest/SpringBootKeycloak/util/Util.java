package com.api.rest.SpringBootKeycloak.util;

import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;



public class Util {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] join(byte[][] matrix){
        // Calculamos el tamano total del arreglo final
        int totalLength = 0;
        for (byte[] array : matrix) {
            totalLength += array.length;
        }

        // Creamos un arreglo unidimensional con el tama�o total
        byte[] result = new byte[totalLength];

        // Llenamos el arreglo unidimensional con los valores de la matriz
        int currentIndex = 0;
        for (byte[] array : matrix) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }

        return result;
    }

    // M�todo para convertir un String a una llave p�blica
    public static PublicKey convertStringToPublicKey(String publicKeyString) throws Exception {
        // Decodificar el String Base64 a bytes
        System.out.println("Mir� la llave que me lleg�: "+publicKeyString);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);

        // Crear un X509EncodedKeySpec a partir de los bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        // Reconstruir la llave p�blica usando KeyFactory
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    public static String convertPublickeyToString(PublicKey publicKey) {
        // Obtener los bytes de la llave privada
        byte[] privateKeyBytes = publicKey.getEncoded();

        // Codificar los bytes a Base64
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }


    public static byte[][] split(byte[] bite, int sizeArray){

        int fragmentos= (bite.length/sizeArray);
        // Sabe cuanto es el residuo para saber cuanto se recorre en la ultima fila
        int lastFrag= bite.length%sizeArray;
        // Si no hay residuo no se agrega fila
        int col=lastFrag==0?fragmentos:fragmentos+1;
        int cont=0;
        byte[][] b= new byte[col][sizeArray];
        for(int i=0;i<col;i++){
            if(i==col-1 && col!=fragmentos){
                sizeArray=lastFrag;
            }
            for(int j=0;j<sizeArray;j++){
                b[i][j] = bite[cont++];
            }
        }
        return b;
    }
    public static ArrayList<byte[]> splitC(byte[] bites, int blockSize){
        ArrayList<byte[]> bloques = new ArrayList<>();

            int totalBytes = bites.length;
            int start = 0;

            while (start < totalBytes) {
                // Calcula el tama�o de cada bloque, asegur�ndote de no exceder el l�mite
                int end = Math.min(start + blockSize, totalBytes);

                // Crea un nuevo bloque y lo a�ade a la lista
                byte[] bloque = Arrays.copyOfRange(bites, start, end);
                bloques.add(bloque);

                // Avanza al siguiente bloque
                start += blockSize;
            }

            return bloques;

    }
    public static String byteArrayToHexString(byte[] bytes, String separator){
        String result = "";
        for (int i=0; i<bytes.length; i++) {
            result += String.format("%02x", bytes[i]) + separator;
        }
        return result.toString();
    }

    public static String getHashFile(String filename, String algorithm)throws Exception{
        MessageDigest hascher = MessageDigest.getInstance(algorithm);

        FileInputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];

        int in;
        while ((in = fis.read(buffer)) != -1) {
            hascher.update(buffer, 0, in);
        }
        fis.close();
        return byteArrayToHexString(hascher.digest(), "");
    }

    public static void saveObject(Object o, String fileName) throws IOException {
        FileOutputStream fileOut;
        ObjectOutputStream out;

        fileOut = new FileOutputStream(fileName);
        out = new ObjectOutputStream(fileOut);

        out.writeObject(o);

        out.flush();
        out.close();
    }

    public static Object loadObject(String fileName) throws IOException, ClassNotFoundException,
            InterruptedException {
        FileInputStream fileIn;
        ObjectInputStream in;

        fileIn = new FileInputStream(fileName);
        in = new ObjectInputStream(fileIn);

        Thread.sleep(100);

        Object o= in.readObject();

        fileIn.close();
        in.close();
        return o;
    }

    public static byte[] objectToByteArray(Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(o);
        out.close();
        byte[] buffer = baos.toByteArray();
        return buffer;
    }

    public static Object byteArrayToObject(byte[] b) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(b));
        Object o = in.readObject();
        in.close();
        return o;
    }


    public static String printKey(PrivateKey privateKey){
        String privateKeyString = "";
        Base64.Encoder encoder = Base64.getEncoder();
        String beginLine = "-----BEGIN PRIVATE KEY-----";
        String endLine = "-----END PRIVATE KEY-----";
        String key64 = encoder.encodeToString(privateKey.getEncoded());
        privateKeyString = beginLine;
        System.out.println(beginLine);
        for (int i = 0; i < key64.length(); i+=64) {
            if (i+64 > key64.length()) {
                String line =key64.substring(i, key64.length());
                privateKeyString += line+"\n";
                        System.out.println(line);
            }else {
                String line =key64.substring(i, i + 64);
                privateKeyString += line+"\n";
                        System.out.println(line);
            }}
        privateKeyString += endLine;
        System.out.println(endLine);

        return privateKeyString;
    }

    public static String printPublicKey(String publicKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        String publicKeyFile="";
        String beginLine = "-----BEGIN PUBLIC KEY-----";
        String endLine = "-----END PUBLIC KEY-----";
        String key64 = encoder.encodeToString(publicKey.getBytes());

        //System.out.println(beginLine);
        publicKeyFile +=beginLine+"\n";
        for (int i = 0; i < key64.length(); i+=64) {
            if (i+64 > key64.length()) {
                String linea = key64.substring(i, key64.length());
                publicKeyFile +=linea+"\n";
                //    System.out.println(linea);
            } else {
                String linea = key64.substring(i, i + 64);
                publicKeyFile +=linea+"\n";
                //      System.out.println(linea);
            }}
        publicKeyFile +=endLine;
        //System.out.println(endLine);

        return publicKeyFile;
    }
    public static String printPublicKey(PublicKey publicKey){
        Base64.Encoder encoder = Base64.getEncoder();
        String publicKeyFile="";
        String beginLine = "-----BEGIN PUBLIC KEY-----";
        String endLine = "-----END PUBLIC KEY-----";
        String key64 = encoder.encodeToString(publicKey.getEncoded());

        System.out.println(beginLine);
        publicKeyFile +=beginLine+"\n";
        for (int i = 0; i < key64.length(); i+=64) {
            if (i+64 > key64.length()) {
                String linea = key64.substring(i, key64.length());
                publicKeyFile +=linea+"\n";
            //    System.out.println(linea);
            } else {
                String linea = key64.substring(i, i + 64);
                publicKeyFile +=linea+"\n";
          //      System.out.println(linea);
            }}
        publicKeyFile +=endLine;
        //System.out.println(endLine);
        System.out.println(publicKeyFile);
        return publicKeyFile;
    }

    public static String generateRandomString(int stringSize){
        if (stringSize < 1) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder randomString = new StringBuilder(stringSize);
        for (int i = 0; i < stringSize; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(randomIndex));
        }

        return randomString.toString();

    }



}
