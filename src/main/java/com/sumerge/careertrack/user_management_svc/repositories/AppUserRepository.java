package com.sumerge.careertrack.user_management_svc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import com.sumerge.careertrack.user_management_svc.entities.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

}
