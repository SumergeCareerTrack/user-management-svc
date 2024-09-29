package com.sumerge.careertrack.user_management_svc.mappers;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppUserRequestDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;

    private String departmentName;

    private String title;
}
