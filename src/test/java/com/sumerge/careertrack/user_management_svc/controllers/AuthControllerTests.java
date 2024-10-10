package com.sumerge.careertrack.user_management_svc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumerge.careertrack.user_management_svc.controllers.AuthController;
import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.entities.responses.AuthenticationResponse;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.exceptions.InvalidCredentialsException;
import com.sumerge.careertrack.user_management_svc.services.AuthService;
import com.sumerge.careertrack.user_management_svc.services.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = { AuthController.class })
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @MockBean
        private JwtService jwtService;

        private ObjectMapper objectMapper;

        private RegisterRequest registerRequest;
        private AppUser managerUser;
        private AppUser testUser;
        private AuthenticationRequest authRequest;

        @BeforeEach
        public void setUp() {
                objectMapper = new ObjectMapper();
                this.authRequest = new AuthenticationRequest("test@test.com", "wrongPassword");

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

                setUpAttributes(department, titleManager, titleEmployee);

        }

        public void setUpAttributes(Department department, Title titleManager, Title titleEmployee) {
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
                                .password("test123")
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

        // TODO 01: Ask others if we have to add DoesNotExist Exception on Department
        // and Title
        @Test
        public void register_CorrectRequest_ReturnsAuthenticationResponse() throws Exception {
                String jwtToken = "Test";
                when(jwtService.extractUserEmail(anyString())).thenReturn("test@test.com");
                when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);
                when(authService.register(registerRequest)).thenReturn(new AuthenticationResponse(jwtToken));

                ResultActions response = mockMvc.perform(post("/auth/register")
                                .header("Authorization", "Bearer TOKEN")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("{\"token\":\"Test\"}"));
        }

        @Test
        public void register_EmailAlreadyExists_ThrowsAlreadyExistsException() throws Exception {
                when(authService.register(this.registerRequest)).thenThrow(new AlreadyExistsException(
                                AlreadyExistsException.APP_USER_EMAIL, this.registerRequest.getEmail()));

                ResultActions response = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(this.registerRequest)));

                response.andExpect(status().isBadRequest())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof AlreadyExistsException))
                                .andExpect(content().string(String.format(AlreadyExistsException.APP_USER_EMAIL,
                                                this.registerRequest.getEmail())));
        }

        @Test
        public void register_WrongDepartment_ThrowsDoesNotExistException() throws Exception {
                when(authService.register(this.registerRequest)).thenThrow(new DoesNotExistException(
                                DoesNotExistException.DEPARTMENT, this.testUser.getDepartment().getName()));

                ResultActions response = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(this.registerRequest)));

                response.andExpect(status().isBadRequest())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof DoesNotExistException))
                                .andExpect(content().string(String.format(DoesNotExistException.DEPARTMENT,
                                                this.testUser.getDepartment().getName())));
        }

        @Test
        public void register_WrongTitle_ThrowsDoesNotExistException() throws Exception {
                when(authService.register(this.registerRequest)).thenThrow(new DoesNotExistException(
                                DoesNotExistException.TITLE, this.testUser.getTitle().getName(),
                                this.testUser.getDepartment().getName()));

                ResultActions response = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(this.registerRequest)));

                response.andExpect(status().isBadRequest())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof DoesNotExistException))
                                .andExpect(content().string(String.format(DoesNotExistException.TITLE,
                                                this.testUser.getTitle().getName(),
                                                this.testUser.getDepartment().getName())));
        }

        @Test
        public void register_WrongManager_ThrowsDoesNotExistException() throws Exception {
                when(authService.register(this.registerRequest)).thenThrow(
                                new DoesNotExistException(DoesNotExistException.APP_USER_ID, this.testUser.getId()));

                ResultActions response = mockMvc.perform(post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(this.registerRequest)));

                response.andExpect(status().isBadRequest())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof DoesNotExistException))
                                .andExpect(content().string(String.format(DoesNotExistException.APP_USER_ID,
                                                this.testUser.getId())));
        }

        @Test
        public void login_ValidCredentials_ReturnsAuthenticationResponse() throws Exception {
                String jwtToken = "Test";

                when(authService.login(authRequest)).thenReturn(new AuthenticationResponse(jwtToken));

                ResultActions response = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)));

                response.andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json("{\"token\":\"Test\"}"));
        }

        /*
         * TODO 02: Why do we have DoesNotExist As well as InvalidCredentials ??? Check
         * login in AuthService
         */
        @Test
        public void login_WrongEmail_ThrowsDoesNotExist() throws Exception {

                when(authService.login(authRequest)).thenThrow(new DoesNotExistException(
                                String.format(DoesNotExistException.APP_USER_EMAIL, authRequest.getEmail())));

                ResultActions response = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)));

                response.andExpect(status().isBadRequest())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof DoesNotExistException))
                                .andExpect(content().string(String.format(DoesNotExistException.APP_USER_EMAIL,
                                                authRequest.getEmail())));
        }

        @Test
        public void login_InvalidCredentials_ThrowsInvalidCredentials() throws Exception {

                when(authService.login(authRequest))
                                .thenThrow(new InvalidCredentialsException(InvalidCredentialsException.DEFAULT));

                ResultActions response = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(authRequest)));

                response.andExpect(status().isForbidden())
                                .andExpect(result -> assertTrue(
                                                result.getResolvedException() instanceof InvalidCredentialsException))
                                .andExpect(content().string((InvalidCredentialsException.DEFAULT)));
        }

        @Test
        public void logout_ValidRequest_ReturnsOk() throws Exception {
                ResultActions response = mockMvc.perform(post("/auth/logout/{email}", this.testUser.getEmail())
                                .header("Authorization", "Bearer TOKEN")
                                .contentType(MediaType.APPLICATION_JSON));
                response.andExpect(status().isOk());
        }
}
