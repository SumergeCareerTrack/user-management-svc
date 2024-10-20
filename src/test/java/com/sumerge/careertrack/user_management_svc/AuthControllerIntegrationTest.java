package com.sumerge.careertrack.user_management_svc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.requests.AuthenticationRequest;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.exceptions.InvalidCredentialsException;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import com.sumerge.careertrack.user_management_svc.services.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@TestPropertySource("classpath:application-tests.properties")
public class AuthControllerIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private DepartmentRepository deptRepository;

    @Autowired
    private TitleRepository titleRepository;

    private Department savedDepartment;
    private Title savedTitle;
    private AppUser savedManager;
    private String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        savedDepartment=Department.builder().name("IT Department").build();
        savedDepartment = deptRepository.save(savedDepartment);
        savedTitle=Title.builder().name("Developer").department(savedDepartment).build();
        savedTitle = titleRepository.save(savedTitle);

        savedManager = appUserRepository.save(AppUser.builder()
                .email("manager@example.com")
                .password("password")
                .firstName("Manager")
                .lastName("User")
                .title(savedTitle)
                .department(savedDepartment)
                .build());

        String loginJson = "{ \"email\": \"email@email.com\", \"password\": \"password\" }";
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        jwtToken = JsonPath.parse(responseJson).read("$.token");
    }

    @AfterEach
    public void tearDown() {
        appUserRepository.delete(this.savedManager);
        titleRepository.delete(this.savedTitle);
        deptRepository.delete(this.savedDepartment);
    }
    @Test
    public void register_CorrectRequest_ReturnsAuthenticationResponse() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDepartment(savedDepartment.getId());
        registerRequest.setTitle(savedTitle.getId());
        registerRequest.setManagerId(savedManager.getId());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
        appUserRepository.delete(appUserRepository.findByEmail(registerRequest.getEmail()).get());
    }

    @Test
    public void register_EmailAlreadyExists_ThrowsAlreadyExistsException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(savedManager.getEmail());
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDepartment(savedDepartment.getId());
        registerRequest.setTitle(savedTitle.getId());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistsException))
                .andExpect(result -> assertEquals("User with email \""+registerRequest.getEmail()+"\" already exists.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void register_WrongDepartment_ThrowsDoesNotExistException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDepartment(UUID.randomUUID());
        registerRequest.setTitle(savedTitle.getId());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DoesNotExistException))
                .andExpect(result -> assertEquals("Department \""+registerRequest.getDepartment()+"\" does not exist.",
                        result.getResolvedException().getMessage()));

    }

    @Test
    public void register_WrongTitle_ThrowsDoesNotExistException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDepartment(savedDepartment.getId());
        registerRequest.setTitle(UUID.randomUUID());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DoesNotExistException))
                .andExpect(result -> assertEquals("Title \""+registerRequest.getTitle()+"\" in department \""+savedDepartment.getName()+"\" does not exist.",
                        result.getResolvedException().getMessage()));
    }


    @Test
    public void register_WrongManager_ThrowsDoesNotExistException() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setDepartment(savedDepartment.getId());
        registerRequest.setTitle(savedTitle.getId());
        registerRequest.setManagerId(UUID.randomUUID());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DoesNotExistException))
                .andExpect(result -> assertEquals("User with ID \""+registerRequest.getManagerId()+"\" does not exist.",
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void login_ValidCredentials_ReturnsAuthenticationResponse() throws Exception {
        AuthenticationRequest loginRequest = new AuthenticationRequest("email@email.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void login_WrongEmail_ThrowsDoesNotExist() throws Exception {
        AuthenticationRequest loginRequest = new AuthenticationRequest("wrong@example.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof InvalidCredentialsException))
                .andExpect(result -> assertEquals("Invalid username or password.",
                        result.getResolvedException().getMessage()));
    }
    @Test
    public void login_InvalidCredentials_ThrowsInvalidCredentials() throws Exception {
        AuthenticationRequest loginRequest = new AuthenticationRequest("manager@example.com", "wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }
    @Test
    public void logout_Success_NoContentResponse() throws Exception {
        mockMvc.perform(post("/auth/logout/" + savedManager.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

}
