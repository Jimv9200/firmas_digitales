package com.api.rest.SpringBootKeycloak.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "publickey")
public class PublicKeyEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Lob // Anotación para campos grandes
    @Column(columnDefinition = "TEXT") // Para bases de datos que lo soporten
    private String publicKey;


    @OneToOne
    @JoinColumn(name = "user")
    private UserEntity user;
}
