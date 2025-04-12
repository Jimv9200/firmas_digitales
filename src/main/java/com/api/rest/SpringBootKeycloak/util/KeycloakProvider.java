package com.api.rest.SpringBootKeycloak.util;

import com.api.rest.SpringBootKeycloak.service.IKeycloakService;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class KeycloakProvider {

    @Value("${mi.variable.ejemplo}")
    private String keycloakUrl;


    private static final String SERVER_URL = "http://localhost:9090";
    private static final String REALM_NAME = "spring-boot-real-dev";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String USER_CONSOLE = "admin";
    private static final String PASSWORD_CONSOLE = "admin";
    private static final String CLIENT_SECRET = "admin";
    //private static final String CLIENT_SECRET = "eN2lYJVY3UPEzH2cnMqKv14DDIa1kUBq";
    private static KeycloakProvider instance;

    public KeycloakProvider() {
        instance = this;
    }

    public static RealmResource getRealmResource() {


        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(instance.keycloakUrl)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();

        return keycloak.realm(REALM_NAME);
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }
}
