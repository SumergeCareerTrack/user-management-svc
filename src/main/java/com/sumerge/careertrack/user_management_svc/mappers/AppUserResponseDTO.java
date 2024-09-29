package com.sumerge.careertrack.user_management_svc.mappers;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppUserResponseDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String departmentName;

    private String title;

    private UUID managerId;
}
