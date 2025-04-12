package com.api.rest.SpringBootKeycloak.controllers;
import com.api.rest.SpringBootKeycloak.service.DigitalSignatureApp;
import com.api.rest.SpringBootKeycloak.entities.UserEntity;
import com.api.rest.SpringBootKeycloak.keys.KeyGeneratorUtil;
import com.api.rest.SpringBootKeycloak.repositories.PublicKeyRepository;
import com.api.rest.SpringBootKeycloak.repositories.UserReposiroty;
import com.api.rest.SpringBootKeycloak.service.EncryptionUtil;
import com.api.rest.SpringBootKeycloak.service.IKeycloakService;
import com.api.rest.SpringBootKeycloak.service.impl.KeycloakServiceImpl;
import com.api.rest.SpringBootKeycloak.service.impl.web.PerfilService;
import com.api.rest.SpringBootKeycloak.util.Util;
import jakarta.servlet.http.HttpSession;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@SessionScope
public class AuthController {


    private Model model;
    @Autowired
    private UserReposiroty userReposiroty;
    @Autowired
    private PublicKeyRepository publicKeyRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private IKeycloakService keycloakService;

    @Value("${mi.variable.ejemplo}")
    private String keycloakUrl;




    private static final String SERVER_URL = "http://192.168.1.2:9090"; // URL del servidor de Keycloak

    private static final String REALM = "spring-boot-real-dev"; // Nombre del realm
    private static final String CLIENT_ID = "spring-client-api-rest"; // Nombre del cliente configurado en Keycloak
    private static final String CLIENT_SECRET = "eN2lYJVY3UPEzH2cnMqKv14DDIa1kUBq"; // El secreto del cliente (si es confidencial)


    @PostMapping("/loginjwt")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials, Model model) {

        String username = credentials.get("username");
        String password = credentials.get("password");
        System.out.println("entrando a autenticar");
        System.out.println("en la direccion: "+keycloakUrl);
        Map<String, String> response = new HashMap<>();
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(keycloakUrl)
                    .realm(REALM)
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(CLIENT_ID)
                    .clientSecret(CLIENT_SECRET)
                    .username(username)
                    .password(password)

                    .build();

            String email="";


            List<UserRepresentation> u=keycloakService.searchUserByUsername(username);
            email=u.get(0).getEmail();
            System.out.println("el email es: "+email);
            String nombreApellido= u.get(0).getFirstName()+" "+ u.get(0).getLastName();
            model.addAttribute("name", nombreApellido);

            model.addAttribute("email", email);
            model.addAttribute("photo", "https://www.iconpacks.net/icons/2/free-icon-user-3296.png");

            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            response.put("token", tokenResponse.getToken());

            System.out.println(tokenResponse.getToken());

            perfilService.crearPerfil(email,username,model,userReposiroty,publicKeyRepository);
            this.model=model;
            System.out.println("se autentico mail");
            response.put("token", tokenResponse.getToken());

        } catch (Exception e) {
            response.put("error", "Autenticación fallida");
        }
        return ResponseEntity.ok("Autenticacion exitosa");
    }
    @GetMapping("/profile")
    public String profile(OAuth2AuthenticationToken token, Model model) throws Exception {



        model.addAttribute("name", token.getPrincipal().getAttribute("name"));
        String username=token.getPrincipal().getAttribute("name");
        model.addAttribute("email", token.getPrincipal().getAttribute("email"));
        String email=token.getPrincipal().getAttribute("email");
        model.addAttribute("photo", token.getPrincipal().getAttribute("picture"));

        perfilService.crearPerfil(email,username,model,userReposiroty,publicKeyRepository);

        this.model = model;



        return "user-profile";
    }

    @PostMapping("/confirmarfirma")
    public ResponseEntity<String> confirmarFirma(@RequestParam("usuario") String userreceived,
                                                 @RequestParam("archivoTabla") String archivoTabla,
                                                 @RequestPart("remitente") String owner) throws Exception {
        UserEntity user= userReposiroty.findByAlias(owner);
        String filename= archivoTabla.split("-")[1];
        File file = new File("src/main/webapp/usuario/"+userreceived+"/received/"+archivoTabla);
        DigitalSignatureApp digitalSignatureApp= new DigitalSignatureApp();
        if(digitalSignatureApp.verificarFirma(user,filename,file)){
            return ResponseEntity.ok("COmprobacion de mensaje correcto");
        }

        return null;
    }


    @PostMapping("/recibo")
    public ResponseEntity<String> recibirDatos(
            @RequestParam("mensaje") String mensaje,
            @RequestParam("archivoTabla") String archivoTabla,
            @RequestPart("archivoSeleccionado") MultipartFile archivoSeleccionado) {
        try {
            System.out.println("Mensaje: " + mensaje);//nombre de usuario para quien va el archivo
            System.out.println("Archivo de la tabla: " + archivoTabla);//el archivo que se va a compartir que esta en firmados
            System.out.println("Archivo seleccionado: " + archivoSeleccionado.getOriginalFilename());//la llave privada
            UserEntity user= userReposiroty.findByAlias(mensaje);//revisa si el usuario existe

            if(user==null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error el usuario no existe");
            }

            // Procesa o guarda el archivo seleccionado
            //crea los archivos en una carpeta temporal
            Path uploadPath = Paths.get("temp/");
            Files.createDirectories(uploadPath);
            //copia la llave que se subio
            Files.copy(archivoSeleccionado.getInputStream(), uploadPath.resolve(archivoSeleccionado.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            //asigna el archivo a una llave
            PrivateKey pv = KeyGeneratorUtil.loadPrivateKeyFromFile("temp/"+archivoSeleccionado.getOriginalFilename());
            //será el nombre del archivo encriptado finalu
            Path fileEcnripted= Paths.get("src/main/webapp/usuario/"+ mensaje+"/received/");
            Files.createDirectories(fileEcnripted);
            Long l= userReposiroty.findByUsername(model.getAttribute("email").toString()).getId();
            File encriptado= new File("src/main/webapp/usuario/"+ mensaje+"/received/"+l.toString()+"-"+archivoTabla+".enc");
            //obtiene el nombre de usuario dueno de la sesion
            String userAlias= model.getAttribute("userAlias")+"";
            System.out.printf("voy a convertir la llave");
            //asigna la llave publica del usuario dueno de la sesion

            System.out.println("publicKey: la convertí");
            //nombre final del archivo desencriptado

            System.out.println("todo bien hasta aqui");
            File archivo=new File("src/main/webapp/usuario/"+userAlias+"/firmados/"+archivoTabla);
            encryptionUtil.encryptFile(archivo,encriptado,pv);
            //encryptionUtil.decryptFile(encriptado,archivoDecript, publicKey);
            return ResponseEntity.ok("Datos recibidos correctamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar los datos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("El archivo seleccionado no es una llave: " + e.getMessage());
        }
    }

    @PostMapping("/descripted")
    public ResponseEntity<String> descripted(@RequestParam("archivoTabla") String archivoTabla, @RequestParam("user") String user,@RequestParam("userreceived") String usuariorecibe) throws Exception {
        File archivoTabla1= new File("src/main/webapp/usuario/"+ usuariorecibe+"/received/"+archivoTabla);
        System.out.println(archivoTabla1.getPath());
        String nombreOriginal=("src/main/webapp/usuario/"+ usuariorecibe+"/received/"+archivoTabla).split(".enc")[0];
        System.out.println(nombreOriginal);
        File archivo=new File( nombreOriginal);
        PublicKey publicKey=Util.convertStringToPublicKey(userReposiroty.findByAlias(user).getPublicKey().getPublicKey());
        encryptionUtil.decryptFile(archivoTabla1,archivo, publicKey);
        return null;
    }




    //return "user-profile";

    @GetMapping("/")
    public String index() {

        return "index";
    }
    @GetMapping("/login")
    public String login() {


        return "custom-login";
    }


    @GetMapping("/fill")
    public String logueado( Model model) {
        // Obtener el token desde la autenticación
        return "files"; // Esta es la vista donde deberás manejar el token en el frontend
    }
    @GetMapping("/profile-return")
    public String profileReturn(Model model) {

        model.addAttribute("name",this.model.getAttribute("name"));

        model.addAttribute("email", this.model.getAttribute("email"));

        model.addAttribute("photo", this.model.getAttribute("photo"));
        model.addAttribute("phone", "3216371746");
        model.addAttribute("userAlias", this.model.getAttribute("userAlias"));

        return "user-profile";
    }





    @GetMapping("/upload")
    public String logueado2() {



        return "upload"; // Esta es la vista donde deberás manejar el token en el frontend
    }

    @GetMapping("/hola")
    public String hola(Model model) {


        model.addAttribute("name",this.model.getAttribute("name"));

        model.addAttribute("email", this.model.getAttribute("email"));

        model.addAttribute("photo", this.model.getAttribute("photo"));
        model.addAttribute("phone", "3216371746");
        model.addAttribute("userAlias", this.model.getAttribute("userAlias"));
        model.addAttribute("tipo", this.model.getAttribute("tipo"));
        System.out.println("atributo del model: " + this.model.getAttribute("tipo"));
        return "user-profile";
    }

    @GetMapping("/firmados")
    public String getFirmados() {
        return "filessigned";
    }

    @GetMapping("/createuser")
    public String createUser(Model model) {
        return "create-user";
    }

    @GetMapping("/sharedme")
    public String sharedMe(Model model) {
        return "shared";
    }


}
