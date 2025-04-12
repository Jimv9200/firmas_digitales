package com.api.rest.SpringBootKeycloak.service.impl.web;

import com.api.rest.SpringBootKeycloak.entities.UserEntity;
import com.api.rest.SpringBootKeycloak.repositories.PublicKeyRepository;
import com.api.rest.SpringBootKeycloak.repositories.UserReposiroty;
import com.api.rest.SpringBootKeycloak.service.DigitalSignatureApp;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.File;

@Service
public class PerfilService {


    public void crearPerfil(String email, String username, Model model, UserReposiroty userReposiroty, PublicKeyRepository publicKeyRepository) throws Exception {
        UserEntity userPerfil = userReposiroty.findByUsername(email);
        if(userPerfil == null) {
            model.addAttribute("tipo","nuevo");
            System.out.println("Se agrega user a la db");
            String photo= model.getAttribute("photo").toString();
            System.out.println("model photo: " + photo);
            System.out.println("email: " + email+" username: " + username);
            UserEntity user = userReposiroty.save(new UserEntity(email, username,photo));

            model.addAttribute("userAlias", user.getAlias());

            System.out.println(model.getAttribute("userAlias"));
            String ruta="src/main/webapp/usuario/"+user.getAlias();
            System.out.println(ruta);
            File directorio = new File(ruta+"/uploads");//"src/main/resources/static/usuario/"
            File signFolder= new File(ruta+"/signed");
            File keysFolder= new File(ruta+"/keys");
            if(directorio.mkdirs()) {
                System.out.println("se creó una carpeta con el nombre de: " + directorio.getPath());
            }
            if(signFolder.mkdirs()) {
                System.out.println("Se creó una carpeta con el nombre de: " + signFolder.getPath());
            }
            if(keysFolder.mkdirs()) {
                System.out.println("Se creó una carpeta con el nombre de: " + keysFolder.getPath());
                DigitalSignatureApp digitalSignatureApp = new DigitalSignatureApp();
                System.out.println(digitalSignatureApp.crearLLaves(user));
                System.out.println(user.getPublicKey().getPublicKey()+" se guardó la llave deñ usuario");
                publicKeyRepository.save(user.getPublicKey());
            }



        }else {
            model.addAttribute("tipo",null);
            String ruta="src/main/webapp/usuario/"+userPerfil.getAlias();
            File directorio = new File(ruta+"/uploads");//"src/main/resources/static/usuario/"
            File signFolder= new File(ruta+"/signed");
            File keysFolder= new File(ruta+"/keys");
            model.addAttribute("userAlias", userPerfil.getAlias());
            if(directorio.mkdirs()) {
                System.out.println("se creó una carpeta con el nombre de: " + directorio.getPath());
            }
            if(signFolder.mkdirs()) {
                System.out.println("Se creó una carpeta con el nombre de: " + signFolder.getPath());
            }
            if(keysFolder.mkdirs()) {
                System.out.println("Se creó una carpeta con el nombre de: " + keysFolder.getPath());
            }


        }
    }
}
