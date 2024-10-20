package com.sumerge.careertrack.user_management_svc.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.UserToken;

@Repository
public interface UserTokenRepository extends CrudRepository<UserToken, String> {

    boolean existsByEmail(String email);

}
