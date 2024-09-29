package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Title;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "title", source = "appUserDTO", qualifiedByName = "namesToTitle")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "manager", ignore = true)
    AppUser toAppUser(AppUserRequestDTO appUserDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "title", source = "appUser.title.id.name")
    @Mapping(target = "departmentName", source = "appUser.title.id.department")
    AppUserRequestDTO toResponseDTO(AppUser appUser);

    @Named("namesToTitle")
    public static Title namesToTitle(AppUserRequestDTO dto) {
        return new Title(dto.getDepartmentName(), dto.getTitle());
    }

}
