package com.api.rest.SpringBootKeycloak.repositories;

import com.api.rest.SpringBootKeycloak.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReposiroty extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    UserEntity findByAlias(String alias);


}
