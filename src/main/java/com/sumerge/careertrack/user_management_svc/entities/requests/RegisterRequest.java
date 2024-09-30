package com.sumerge.careertrack.user_management_svc.entities.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Data
@Builder
@Service
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UUID managerId;
    private UUID department;
    private UUID title;

}