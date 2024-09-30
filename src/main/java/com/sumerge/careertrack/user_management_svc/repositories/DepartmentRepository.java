package com.sumerge.careertrack.user_management_svc.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.Department;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByName(String name);
}
