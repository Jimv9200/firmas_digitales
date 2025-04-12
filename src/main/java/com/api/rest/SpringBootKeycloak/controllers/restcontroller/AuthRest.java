package com.api.rest.SpringBootKeycloak.controllers.restcontroller;

import com.api.rest.SpringBootKeycloak.entities.UserEntity;
import com.api.rest.SpringBootKeycloak.service.DigitalSignatureApp;
import com.api.rest.SpringBootKeycloak.keys.KeyGeneratorUtil;
import com.api.rest.SpringBootKeycloak.repositories.UserReposiroty;
import com.api.rest.SpringBootKeycloak.service.ImageUploadService;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@SessionScope
public class AuthRest {

    @Autowired
    private UserReposiroty userReposiroty;


    /**
     * Metodo para cargar las imagenes en la carpeta del usuario
     * @param file el archivo que se va a cargar
     * @param user el usuario que está manipulando el sistema
     * @return
     */
    @PostMapping("/upload-image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("user") String user) {
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se ha reconocido el usuario");
        }
        System.out.println("el nombre del usuario es: "+ user);
        ImageUploadService imageUploadService= new ImageUploadService(user);
        try {
            String filePath = imageUploadService.uploadImage(file);
            System.out.println("Se subio la foto");
            return ResponseEntity.ok("Imagen subida correctamente: " + filePath);
        } catch (IOException e) {
            System.out.println("Error al subir la foto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }

    /**
     * Metodo que muestra los archivo en la carpeta del usuario
     * @param folderPath
     * @return
     */
    @GetMapping("/files")
    public List<String> getFiles(@RequestParam String folderPath) {
        String ruta ="src/main/webapp/usuario/"+folderPath+"/uploads";

        List<String> fileNames = new ArrayList<>();
        File folder = new File(ruta);

        if (folder.exists() && folder.isDirectory()) {
            // Obtiene todos los archivos de la carpeta
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName()); // Añade el nombre del archivo
                    }
                }
            }
        }
        System.out.println("enviando arvhicos");
        return fileNames; // Retorna la lista de archivos
    }

    /**
     * Metodo que muestra los archivo en la carpeta del usuario
     * @param folderPath
     * @return
     */
    @GetMapping("/filessigned")
    public List<String> getFilesSigned(@RequestParam String folderPath) {
        String ruta ="src/main/webapp/usuario/"+folderPath+"/firmados";

        List<String> fileNames = new ArrayList<>();
        File folder = new File(ruta);

        if (folder.exists() && folder.isDirectory()) {
            // Obtiene todos los archivos de la carpeta
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName()); // Añade el nombre del archivo
                    }
                }
            }
        }
        System.out.println("enviando arvhicos");
        return fileNames; // Retorna la lista de archivos
    }

    @GetMapping("/shared")
    public List<String> getSharedFiles(@RequestParam String folderPath) {
        System.out.println("voy a ver archivos de recibidos");
        String ruta ="src/main/webapp/usuario/"+folderPath+"/received";

        List<String> fileNames = new ArrayList<>();
        File folder = new File(ruta);

        if (folder.exists() && folder.isDirectory()) {
            // Obtiene todos los archivos de la carpeta
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileNames.add(file.getName()); // Añade el nombre del archivo
                    }
                }
            }
        }
        System.out.println("enviando arvhicos");
        return fileNames; // Retorna la lista de archivos
    }
    // Método para firmar un archivo
    @PostMapping("/files")
    public String signFile(@RequestParam("folderPath") String folderPath, @RequestParam("fileName") String fileName,
                           @RequestPart("archivoSeleccionado") MultipartFile archivoSeleccionado) throws Exception {
        System.out.println("si señor ya lo voy a firmar");
        DigitalSignatureApp digitalSignatureApp= new DigitalSignatureApp();

        System.out.println(folderPath+" carpeta "+fileName+" archivo");
        System.out.println(archivoSeleccionado.getOriginalFilename());
        PrivateKey pv=KeyGeneratorUtil.loadPrivateKeyFromFile(archivoSeleccionado);
        System.out.println("El usuario es: "+userReposiroty.findByAlias(folderPath).getAlias());
        if(digitalSignatureApp.DigitalSignatureApp( userReposiroty.findByAlias(folderPath),fileName, pv)){
            String path= "src/main/webapp/usuario/"+folderPath+"/";
            File firmado=new File(path+"firmados");
            if(!firmado.exists()){
                firmado.mkdir();
            }
            moverArchivo(path+"uploads/"+fileName,path+"firmados/"+fileName);
            return "Archivo verificado y firmado correctamente";
        }

        return "Error al firmar el archivo";

    }


    public String moverArchivo(String rutaArchivo, String rutaFirmados) {
        Path origen = Paths.get(rutaArchivo);   // Ruta del archivo original
        Path destino = Paths.get(rutaFirmados);   // Ruta de la nueva ubicación

        try {
            Files.move(origen, destino, StandardCopyOption.REPLACE_EXISTING); // Mueve y reemplaza si existe
            System.out.println("Archivo movido exitosamente.");
            return "Archivo movido exitosamente.";
        } catch (IOException e) {
            System.err.println("Error al mover el archivo: " + e.getMessage());
            return "Error al mover el archivo: " + e.getMessage();
        }
    }

    @PostMapping("/searchuser")
    public ResponseEntity<String> buscarUsuario(@RequestParam String username) {
        System.out.println("buscando usuario");
        UserEntity user =userReposiroty.findById(Long.parseLong(username)).orElse(null);
        if(user!=null){
            System.out.println("usuario encontrado");
            return ResponseEntity.ok(user.getAlias());
        }

        return ResponseEntity.notFound().build();

    }


    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String user) throws IOException {
        // Ruta del archivo en tu servidor
        System.out.println("Se van a enviar archivos");


        String filePath = "src/main/webapp/usuario/"+user+"/keys/privateKey.key";
        System.out.println(filePath);

        // Crear un recurso para el archivo
        Resource fileResource = new FileSystemResource(filePath);

        if (!fileResource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Configurar los encabezados para la descarga
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }


    @GetMapping("/setmodel")
    public String setModel(){
        return "ok";
    }

    @GetMapping("/obtenerItems")
    public List<String> obtenerUser(){
        return userReposiroty.findAll().stream().map(UserEntity::getAlias).collect(Collectors.toList());
    }

}
