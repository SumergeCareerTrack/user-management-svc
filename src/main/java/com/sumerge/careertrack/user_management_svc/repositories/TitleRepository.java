package com.sumerge.careertrack.user_management_svc.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.Title;

@Repository
public interface TitleRepository extends JpaRepository<Title, UUID> {
    List<Title> findByDepartmentName(String deptName);

    List<Title> findByName(String name);

    Optional<Title> findByNameAndDepartmentName(String titleName, String deptName);

    boolean existsById(UUID id);

    boolean existsByNameAndDepartmentName(String titleName, String deptName);
}
