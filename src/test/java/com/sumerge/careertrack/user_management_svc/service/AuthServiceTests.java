package com.sumerge.careertrack.user_management_svc.service;


import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.entities.responses.AuthenticationResponse;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.exceptions.InvalidCredentialsException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Autowired
    MockMvc mockMvc;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TitleRepository titleRepository;
    @Mock
    private DepartmentRepository deptRepository;
    @InjectMocks
    private AuthService authService;


    private RegisterRequest registerRequest;
    private AppUser managerUser;
    private AppUser testUser;
    private AuthenticationRequest authRequest;

    public void setUpAttributes(Department department, Title titleManager, Title titleEmployee){
        this.managerUser = AppUser.builder()
                .id(UUID.randomUUID())
                .email("manager@manager.com")
                .password("test123")
                .firstName("Andrew")
                .lastName("Smith")
                .manager(null)
                .department(department)
                .title(titleManager)
                .build();

        this.testUser = AppUser.builder()
                .id(UUID.randomUUID())
                .email("test@test.com")
                .password("password")
                .firstName("Andrew")
                .lastName("Smith")
                .manager(managerUser)
                .department(department)
                .title(titleEmployee)
                .build();
        registerRequest = new RegisterRequest(
                testUser.getEmail(),
                testUser.getPassword(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getDepartment().getId(),
                testUser.getTitle().getId(),
                testUser.getManager().getId());
    }
    @BeforeEach
    public void setUp() {
        this.authRequest = new AuthenticationRequest("test@test.com", "password");

        Department department = Department.builder()
                .id(UUID.randomUUID())
                .name("HR")
                .build();

        Title titleEmployee = Title.builder()
                .id(UUID.randomUUID())
                .name("Employee")
                .isManager(false)
                .build();

        Title titleManager = Title.builder()
                .id(UUID.randomUUID())
                .name("Manager")
                .isManager(true)
                .build();

        setUpAttributes(department,titleManager,titleEmployee);
    }


    @Test
    public void register_RegistersSuccussfully_ReturnsAuthenticationResponse() {
        when(appUserRepository.existsByEmail(anyString())).thenReturn(false);
        when(deptRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getDepartment()));
        when(titleRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getTitle()));
        when(appUserRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getManager()));
        when(appUserRepository.save(any(AppUser.class))).thenReturn(this.testUser);
        when(jwtService.generateToken(any(AppUser.class))).thenReturn("token");
        AuthenticationResponse response = authService.register(this.registerRequest);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("token", response.getToken());
        assertTrue(response.getToken().length() > 0);

    }

    @Test
    public void register_whenUserExists_ThrowsAlreadyExistsException() {

        System.out.println(this.registerRequest.getEmail());  // Print the email to verify it's what you expect

        when(appUserRepository.existsByEmail(any())).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> authService.register(this.registerRequest));
    }
    @Test
    public void register_whenDepartmentNotfound_ThrowsDoesNotExist() {
        when(appUserRepository.existsByEmail(anyString())).thenReturn(false);

        when(deptRepository.findById(any())).thenThrow(new DoesNotExistException(DoesNotExistException.DEPARTMENT, this.registerRequest.getDepartment()));
        assertThrows(DoesNotExistException.class, () -> authService.register(this.registerRequest));

    }
    @Test
    public void register_whenTitleNotfound_ThrowsDoesNotExist() {

        when(appUserRepository.existsByEmail(anyString())).thenReturn(false);
        when(deptRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getDepartment()));
        when(titleRepository.findById(any())).thenThrow(new DoesNotExistException(DoesNotExistException.TITLE, this.registerRequest.getTitle(),this.registerRequest.getDepartment()));
        assertThrows(DoesNotExistException.class, () -> authService.register(this.registerRequest));

    }
    @Test
    public void register_whenManagerNotfound_ThrowsDoesNotExist() {


        when(appUserRepository.existsByEmail(anyString())).thenReturn(false);
        when(deptRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getDepartment()));
        when(titleRepository.findById(any())).thenReturn(Optional.ofNullable(this.testUser.getTitle()));
        when(titleRepository.findById(any())).thenThrow(new DoesNotExistException(DoesNotExistException.APP_USER_ID, this.registerRequest.getManagerId()));
        assertThrows(DoesNotExistException.class, () -> authService.register(this.registerRequest));
    }

    @Test
    public void login_loginSuccussfully_ReturnsAuthenticationResponse() {
        when(appUserRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(testUser)).thenReturn("Token");
        AuthenticationResponse response = authService.login(this.authRequest);
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("Token", response.getToken());
    }

    @Test
    public void login_InvalidCredentials_ThrowsInvalidCredentialsException() {

         authRequest.setPassword("wrongPassword");
        doThrow(InvalidCredentialsException.class)
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
    }

    @Test
    public void login_UserDoesNotExist_ThrowsDoesNotExistException() {
         authRequest.setEmail("nonexistent@test.com");
        when(appUserRepository.findByEmail(authRequest.getEmail())).thenReturn(Optional.empty());
        DoesNotExistException exception = assertThrows(DoesNotExistException.class, () -> authService.login(authRequest));

        assertEquals(String.format(DoesNotExistException.APP_USER_EMAIL,this.authRequest.getEmail()), exception.getMessage());
    }
//TODO 01: Review with youssef If we need a NULL one because it throws Does not exist , It passes the try catch in the Serivce ???
//    @Test
//    public void login_NullCredentials_ThrowsInvalidCredentialsException() {
//
//        AuthenticationRequest authRequest = new AuthenticationRequest(null, "password123");
//
//
//        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
//    }
//
//    @Test
//    public void login_EmptyEmail_ThrowsInvalidCredentialsException() {
//
//        AuthenticationRequest authRequest = new AuthenticationRequest("", "password123");
//
//
//        assertThrows(InvalidCredentialsException.class, () -> authService.login(authRequest));
//    }

}
