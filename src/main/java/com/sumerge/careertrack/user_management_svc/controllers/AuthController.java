package com.sumerge.careertrack.user_management_svc.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.entities.responses.AuthenticationResponse;
import com.sumerge.careertrack.user_management_svc.services.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@PathVariable UUID userId) {
        authService.logout(userId);
    }
}
