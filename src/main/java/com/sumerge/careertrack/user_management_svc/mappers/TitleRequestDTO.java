package com.sumerge.careertrack.user_management_svc.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TitleRequestDTO {
    private String departmentName;

    private String titleName;
}
