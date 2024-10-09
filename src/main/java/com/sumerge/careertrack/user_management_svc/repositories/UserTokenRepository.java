package com.sumerge.careertrack.user_management_svc.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.UserToken;

@Repository
public interface UserTokenRepository extends CrudRepository<UserToken, UUID> {

    boolean existsByEmail(String email);

    Optional<UserToken> findByEmail(String email);

}
