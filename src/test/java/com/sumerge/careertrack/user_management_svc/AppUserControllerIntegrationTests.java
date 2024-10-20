package com.sumerge.careertrack.user_management_svc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sumerge.careertrack.user_management_svc.controllers.AppUserController;
import com.sumerge.careertrack.user_management_svc.controllers.AuthController;
import com.sumerge.careertrack.user_management_svc.controllers.TitleController;
import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.requests.RegisterRequest;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserMapper;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentMapper;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import com.sumerge.careertrack.user_management_svc.repositories.UserTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@TestPropertySource("classpath:application-tests.properties")
public class AppUserControllerIntegrationTests {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TitleRepository titleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    AppUser appUser;
    AppUser manager;
    AppUser testUser;
    Title userTitle;
    Title managerTitle;
    Department department;
    String jwtToken;


    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        department = Department.builder().name("TestDept").build();
        userTitle = Title.builder().name("User").isManager(false).department(department).build();
        managerTitle = Title.builder().name("Manager").isManager(true).department(department).build();
        manager = AppUser.builder()
                .email("Testmanager@email.com").manager(null).firstName("TestManager")
                .lastName("1").password("password").title(managerTitle).department(department).build();
        appUser = AppUser.builder()
                .email("employee@email.com").manager(manager).firstName("TestEmployee")
                .lastName("1").password("password").title(userTitle).department(department).build();
        testUser = AppUser.builder()
                .email("employee@email.com").manager(null).firstName("TestEmployee")
                .lastName("1").password("password").title(userTitle).department(department).build();
        String loginJson = "{ \"email\": \"email@email.com\", \"password\": \"password\" }";
        department =departmentRepository.save(department);
        userTitle=titleRepository.save(userTitle);
        managerTitle=titleRepository.save(managerTitle);
        manager.setTitle(managerTitle);
        manager=appUserRepository.save(manager);
        appUser.setTitle(userTitle);
        appUser.setDepartment(department);
        appUser.setManager(manager);
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
        appUserRepository.deleteById(manager.getId());
        titleRepository.deleteById(userTitle.getId());
        titleRepository.deleteById(managerTitle.getId());
        departmentRepository.deleteById(department.getId());
    }

    @Test
    public void userCreation_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email(appUser.getEmail())
                .title(userTitle.getId())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .department(department.getId())
                .password(appUser.getPassword())
                .managerId(manager.getId())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/email/" + appUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(appUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(appUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(appUser.getLastName()));
        appUserRepository.deleteById(appUserRepository.findByEmail(appUser.getEmail()).get().getId());



    }

    @Test
    public void getById_Success() throws Exception {
        AppUser savedUser = appUserRepository.save(appUser);

        mockMvc.perform(get("/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(savedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(savedUser.getLastName()));
        appUserRepository.deleteById(appUser.getId());

    }
    @Test
    public void getUserByEmail_UserExists_ReturnsUser() throws Exception {
        AppUser savedUser = appUserRepository.save(appUser);
        mockMvc.perform(get("/users/email/"+savedUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
        appUserRepository.deleteById(appUser.getId());
    }
    @Test
    public void getUserByEmail_DoesNotExist_BadRequest() throws Exception {
        mockMvc.perform(get("/users/email/nonexistent@email.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getUserById_DoesNotExist_BadRequest() throws Exception {
        mockMvc.perform(get("/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getBatch_Success() throws Exception {
        AppUser savedUser1 = appUserRepository.save(appUser);
        AppUser savedUser2 = appUserRepository.save(testUser);

        List<UUID> userIds = List.of(savedUser1.getId(), savedUser2.getId());
        String jsonRequest = objectMapper.writeValueAsString(userIds);

        mockMvc.perform(post("/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))  // Expecting two users in the response
                .andExpect(jsonPath("$[0].email").value(savedUser1.getEmail()))
                .andExpect(jsonPath("$[1].email").value(savedUser2.getEmail()));
        appUserRepository.delete(savedUser1);
        appUserRepository.delete(savedUser2);
    }
    @Test
    public void getBatch_EmptyIds_ReturnsEmptyList() throws Exception {
        List<UUID> emptyIds = Collections.emptyList();
        String jsonRequest = objectMapper.writeValueAsString(emptyIds);
        mockMvc.perform(post("/users/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); // Expecting an empty list
    }
    @Test
    public void getSubordinates_Success() throws Exception {

        appUser.setManager(manager);
        appUserRepository.save(appUser);

        mockMvc.perform(get("/users/" + manager.getId() + "/subordinates")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(appUser.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(appUser.getFirstName()));
        appUserRepository.deleteById(appUser.getId());

    }
    @Test
    public void getSubordinates_NoSubordinates_ReturnsEmpty() throws Exception {
        AppUser userWithoutSubordinates = appUserRepository.save(testUser);  

        mockMvc.perform(get("/users/" + userWithoutSubordinates.getId() + "/subordinates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0)); 
        appUserRepository.deleteById(userWithoutSubordinates.getId());
    }
    @Test
    public void getSubordinates_UserDoesNotExist_Throws() throws Exception {
        mockMvc.perform(get("/users/" + UUID.randomUUID() + "/subordinates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getAll_Success() throws Exception {
        appUserRepository.save(appUser);
        appUserRepository.save(manager);

        mockMvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("email@email.com"))
                .andExpect(jsonPath("$[1].email").value(manager.getEmail()))
                .andExpect(jsonPath("$[2].email").value(appUser.getEmail()));
        appUserRepository.deleteById(appUser.getId());

    }


    @Test
    public void getManagersByDept_Success() throws Exception {
        manager.setDepartment(department);
        appUser.setDepartment(department);
        appUserRepository.save(manager);
        appUserRepository.save(appUser);

        mockMvc.perform(get("/users/managers?departmentName=" + department.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(manager.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(manager.getFirstName()));
        appUserRepository.deleteById(appUser.getId());

    }

    @Test
    public void getManagersByDept_NoManagers_EmptyList() throws Exception {
        Department newDept = Department.builder().name("NoManagerDept").build();
        newDept = departmentRepository.save(newDept);

        mockMvc.perform(get("/users/managers")
                        .param("departmentName", newDept.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        departmentRepository.deleteById(newDept.getId());
    }

    @Test
    public void getManagersByDept_DeptDoesNotExist_Throws() throws Exception {
        mockMvc.perform(get("/users/dept/" + UUID.randomUUID() + "/managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllByTitle_Success() throws Exception {
        manager.setTitle(managerTitle);
        appUser.setTitle(userTitle);
        appUserRepository.save(manager);
        appUserRepository.save(appUser);

        mockMvc.perform(get("/users/title/" + userTitle.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(appUser.getEmail()))
                .andExpect(jsonPath("$[0].firstName").value(appUser.getFirstName()));
        appUserRepository.deleteById(appUser.getId());

    }
    @Test
    public void getAllByTitle_NoTitleHolders_ReturnsEmpty() throws Exception {
        Title newTitle = Title.builder().name("EmptyTitle").isManager(false).department(department).build();
        titleRepository.save(newTitle);

        mockMvc.perform(get("/users/title/" + newTitle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        titleRepository.deleteById(newTitle.getId());
    }


    @Test
    public void updateUser_Success() throws Exception {
        AppUser savedUser = appUserRepository.save(appUser);

        AppUserRequestDTO updateUserRequest = AppUserRequestDTO.builder()
                .id(savedUser.getId())
                .email("updated@email.com")
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .managerId(manager.getId())
                .titleId(userTitle.getId())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(updateUserRequest);

        mockMvc.perform(put("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.firstName").value("UpdatedFirstName"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLastName"));
        appUserRepository.deleteById(savedUser.getId());

    }
    @Test
    public void updateUser_UserDoesNotExist_Throws() throws Exception {
        AppUserRequestDTO updateUserRequest = AppUserRequestDTO.builder()
            .id(UUID.randomUUID())
            .email("updated@email.com")
            .firstName("UpdatedFirstName")
            .lastName("UpdatedLastName")
            .managerId(manager.getId())
            .titleId(userTitle.getId())
            .build();

        mockMvc.perform(put("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserRequest.toString())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changePassword_Success() throws Exception {
        AppUser savedUser = appUserRepository.save(appUser);
        String newPassword = "newPassword123";

        mockMvc.perform(put("/users/password/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPassword)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        AppUser updatedUser = appUserRepository.findById(savedUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()),
                "The password was not updated correctly.");

        appUserRepository.deleteById(appUser.getId());
    }
    @Test
    public void changePassword_UserDoesNotExist_Throws() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        String newPassword = "newPassword123";

        mockMvc.perform(put("/users/password/" + nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPassword)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DoesNotExistException));
    }

    @Test
    public void deleteUser_Success() throws Exception {
        AppUser savedUser = appUserRepository.save(appUser);

        mockMvc.perform(delete("/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        assertFalse(appUserRepository.findById(savedUser.getId()).isPresent());
    }
    @Test
    public void deleteUser_UserDoesNotExist_Throws() throws Exception {
        mockMvc.perform(delete("/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }


}
