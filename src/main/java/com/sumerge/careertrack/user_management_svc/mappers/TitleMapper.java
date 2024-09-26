package com.sumerge.careertrack.user_management_svc.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;

@Mapper(componentModel = "spring")
public interface TitleMapper {

    @Mapping(target = "id", source = "titleDTO", qualifiedByName = "namesToTitleId")
    Title toTitle(TitleDTO titleDTO);

    TitleDTO toDTO(Title title);

    @Named("namesToTitleId")
    public static TitleId namesToTitleId(TitleDTO dto) {
        return new TitleId(dto.getDepartmentName(), dto.getTitleName());
    }

}
