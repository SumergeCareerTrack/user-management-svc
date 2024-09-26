package com.sumerge.careertrack.user_management_svc.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TitleDTO {
    private String departmentName;

    private String titleName;

    private boolean isManager;
}
