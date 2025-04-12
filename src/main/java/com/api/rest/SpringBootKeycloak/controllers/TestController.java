package com.api.rest.SpringBootKeycloak.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello-1")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<String> helloAdmin(){
        System.out.println("hello admin");
        return ResponseEntity.ok("hola señor administrador");
    }

    @GetMapping("/saludo2")
    @PreAuthorize("hasRole('admin_client_role')")
    public String saludo(){
        System.out.println("hello admin saludando patrón");
        return "Hola esto es un saludo";
    }

    @GetMapping("/hello-2")
    @PreAuthorize("hasRole('user_client_role') or hasRole('admin_client_role')")
    public String helloUser(){
        return "Hello Sprig Boot With Keycloak with USER";
    }
}
