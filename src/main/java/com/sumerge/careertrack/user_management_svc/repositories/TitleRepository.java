package com.sumerge.careertrack.user_management_svc.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

@Repository
public interface TitleRepository extends JpaRepository<Title, TitleId> {
    List<Title> findByDepartment(String deptName);

    boolean existsByDepartmentAndName(String deptName, String titleName);
}
