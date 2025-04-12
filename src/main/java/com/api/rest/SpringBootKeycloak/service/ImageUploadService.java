package com.api.rest.SpringBootKeycloak.service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUploadService {

    private String upload_directory; //= //"src/main/resources/usuario"; // Ruta donde se almacenarán las imágenes
    public ImageUploadService(String upload_directory) {
        this.upload_directory="src/main/webapp/usuario/"+upload_directory+"/uploads/";

    }

    public String uploadImage(MultipartFile file) throws IOException {
        // Verifica si el archivo está vacío
        System.out.println("se va a guardar la foto en: "+upload_directory);
        if (file.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }

        // Crear el path de destino
        Path uploadPath = Paths.get(upload_directory);

        // Crear la carpeta de destino si no existe
        try {
            Files.createDirectories(uploadPath); // Crea las carpetas necesarias
        } catch (IOException e) {
            System.err.println("Error al crear el directorio: " + e.getMessage());
            throw new IOException("No se pudo crear el directorio de carga", e);
        }

        // Crear el nombre del archivo con un timestamp para evitar colisiones
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName); // Obtiene el path completo para el archivo

        // Escribir los bytes del archivo en el archivo de destino
        try (BufferedOutputStream toFile = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
            // Escribir el contenido del archivo
            toFile.write(file.getBytes());
            toFile.flush(); // Asegurarse de que los datos se escriban


        } catch (IOException e) {
            System.err.println("Error al guardar el archivo: " + e.getMessage());
            throw new IOException("Error al guardar el archivo", e);
        }

        return "success";
    }
}

