package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sumerge.careertrack.user_management_svc.entities.Title;

@Mapper(componentModel = "spring")
public interface TitleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "isManager", source = "manager")
    Title toTitle(TitleRequestDTO titleDTO);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "isManager", source = "manager")
    @Mapping(target = "name", source = "name")
    TitleResponseDTO toDTO(Title title);

}
