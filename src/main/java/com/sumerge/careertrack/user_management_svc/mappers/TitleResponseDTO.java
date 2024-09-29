package com.sumerge.careertrack.user_management_svc.mappers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TitleResponseDTO {
    private String departmentName;

    private String titleName;

    private boolean isManager;
}
