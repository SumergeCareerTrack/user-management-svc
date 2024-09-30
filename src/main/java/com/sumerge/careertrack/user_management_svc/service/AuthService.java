package com.sumerge.careertrack.user_management_svc.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.entities.responses.AuthenticationResponse;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TitleRepository titleRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        var newUser = AppUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .manager(appUserRepository.findById(request.getManagerId()).get())
                .title(titleRepository.findById(request.getTitle()).get())
                .department(titleRepository.findById(request.getTitle()).get().getDepartment())
                .build();
        if (appUserRepository.findByEmail(request.getEmail()).isPresent())
            throw new AlreadyExistsException(
                    AlreadyExistsException.APP_USER_EMAIL, request.getEmail());
        else {
            appUserRepository.save(newUser);
            var jwtToken = jwtService.generateToken(newUser);
            jwtService.saveTokenInRedis(request.getEmail(), jwtToken);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
    }

    // TODO: Review Exception
    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }

        var user = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_EMAIL, request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        jwtService.saveTokenInRedis(request.getEmail(), jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user.getEmail())
                .build();
    }

}
