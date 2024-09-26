package com.sumerge.careertrack.user_management_svc.repositories;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    List<AppUser> findByTitle(String titleName);

    List<AppUser> findAllByTitleId(TitleId deptAndTitle);

    List<AppUser> findAllByManager(AppUser manager);

}
