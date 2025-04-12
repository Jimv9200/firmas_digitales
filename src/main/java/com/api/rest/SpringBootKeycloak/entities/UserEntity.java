package com.api.rest.SpringBootKeycloak.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @Email
    private String username;
    @Column(nullable = false)

    private String nombre;
    @Size( max = 120)
    private String alias;

    private String photo;


    @OneToOne(mappedBy = "user")
    private PublicKeyEntity publicKey;

    public UserEntity(String username, String nombre, String photo) {
        alias=username.split("@")[0]+username.split("@")[1].replace(".","");
        this.username = username;
        this.nombre = nombre;
        this.photo = photo;
    }

}
