package com.api.rest.SpringBootKeycloak.util;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;

import java.util.Scanner;

public class KeycloakConsoleLogin {

    private static final String SERVER_URL = "http://localhost:9090"; // URL del servidor de Keycloak
    private static final String REALM = "spring-boot-real-dev"; // Nombre del realm
    private static final String CLIENT_ID = "spring-client-api-rest"; // Nombre del cliente configurado en Keycloak
    private static final String CLIENT_SECRET = "eN2lYJVY3UPEzH2cnMqKv14DDIa1kUBq"; // El secreto del cliente (si es confidencial)

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingresa tu nombre de usuario: ");
        String username = scanner.nextLine();

        System.out.print("Ingresa tu contraseña: ");
        String password = scanner.nextLine();

        // Llama al método para autenticar al usuario y obtener el token
        try {
            String token = autenticarYObtenerToken(username, password);
            System.out.println("Token de acceso obtenido: " + token);
        } catch (Exception e) {
            System.err.println("Error al autenticar: " + e.getMessage());
        }
    }

    private static String autenticarYObtenerToken(String username, String password) {
        // Construye la instancia de Keycloak para la autenticación
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username(username)
                .password(password)
                .build();

        // Obtiene el token de acceso
        AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
        System.out.println("Access Token: " + tokenResponse.getToken());
        System.out.println("Refresh Token: " + tokenResponse.getRefreshToken());
        System.out.println("Expires In: " + tokenResponse.getExpiresIn());
        return tokenResponse.getToken();
    }
}