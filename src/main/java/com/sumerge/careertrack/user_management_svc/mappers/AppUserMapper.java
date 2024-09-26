package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;

@Mapper
public interface AppUserMapper {
    AppUser toAppUser(AppUserDTO appUserDTO);

    AppUserDTO toDTO(AppUser appUser);

}
