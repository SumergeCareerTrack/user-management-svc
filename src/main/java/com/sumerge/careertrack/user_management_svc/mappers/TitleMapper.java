package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

@Mapper(componentModel = "spring")
public interface TitleMapper {

    @Mapping(target = "id", source = "titleDTO", qualifiedByName = "namesToTitleId")
    @Mapping(target = "isManager", ignore = true)
    Title toTitle(TitleRequestDTO titleDTO);

    @Mapping(target = "departmentName", source = "id.department")
    @Mapping(target = "titleName", source = "id.name")
    @Mapping(target = "isManager", source = "manager")
    TitleResponseDTO toDTO(Title title);

    @Named("namesToTitleId")
    public static TitleId namesToTitleId(TitleRequestDTO dto) {
        return new TitleId(dto.getDepartmentName(), dto.getTitleName());
    }

}
