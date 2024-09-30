package com.sumerge.careertrack.user_management_svc.mappers;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRequestDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private UUID departmentId;

    private UUID titleId;

    private UUID managerId;
}
