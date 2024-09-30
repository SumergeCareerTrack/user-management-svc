package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;

import com.sumerge.careertrack.user_management_svc.entities.Department;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentResponseDTO toDTO(Department department);

}
