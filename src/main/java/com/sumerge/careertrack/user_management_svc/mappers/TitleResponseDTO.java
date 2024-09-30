package com.sumerge.careertrack.user_management_svc.mappers;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TitleResponseDTO {
    private UUID id;

    private UUID departmentId;

    private String titleName;

    private boolean isManager;
}
