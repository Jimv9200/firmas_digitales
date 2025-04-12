package com.api.rest.SpringBootKeycloak.repositories;

import com.api.rest.SpringBootKeycloak.entities.PublicKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicKeyRepository extends JpaRepository<PublicKeyEntity,Long> {

}
