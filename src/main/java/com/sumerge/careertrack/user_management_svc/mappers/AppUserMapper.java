package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "title", ignore = true)
    @Mapping(target = "manager", ignore = true)
    AppUser toAppUser(AppUserRequestDTO appUserDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "title", source = "appUser.title")
    @Mapping(target = "department", source = "appUser.department")
    @Mapping(target = "managerId", source = "appUser.manager.id")
    AppUserResponseDTO toResponseDTO(AppUser appUser);
}
