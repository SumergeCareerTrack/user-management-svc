package com.sumerge.careertrack.user_management_svc.mappers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TitleRequestDTO {
    private String departmentName;

    private String titleName;
}
