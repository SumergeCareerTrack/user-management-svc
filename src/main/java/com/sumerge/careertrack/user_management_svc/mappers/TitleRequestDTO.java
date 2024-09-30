package com.sumerge.careertrack.user_management_svc.mappers;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TitleRequestDTO {
    @lombok.NonNull
    private UUID departmentId;

    @lombok.NonNull
    private String name;

    private boolean isManager;
}
