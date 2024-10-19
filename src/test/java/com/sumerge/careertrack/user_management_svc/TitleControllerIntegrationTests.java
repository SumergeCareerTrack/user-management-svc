package com.sumerge.careertrack.user_management_svc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@TestPropertySource("classpath:application-tests.properties")
public class TitleControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentRequestDTO departmentRequestDTO;

    private Title savedTitle;
    private Department savedDepartment;
    String jwtToken;

    @BeforeEach
    public void setUp() throws Exception {
        departmentRequestDTO = new DepartmentRequestDTO();
        departmentRequestDTO.setName("Test Department");
        savedDepartment=Department.builder().name("Test Department").build();
        savedDepartment = departmentRepository.save(savedDepartment);

        savedTitle=Title.builder().name("Test Title").department(savedDepartment).build();

        savedTitle = titleRepository.save(savedTitle);
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
        titleRepository.delete(savedTitle);
        departmentRepository.delete(savedDepartment);
    }

    /* Test cases */

    @Test
    public void getAllTitles_Successful() throws Exception {
        mockMvc.perform(get("/titles/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void getAllTitles_Not_Successful() throws Exception {
        titleRepository.delete(savedTitle);
        mockMvc.perform(get("/titles/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                //Returns 1 because we have a manual entry inside if not it will be 0 and an empty list
                .andExpect(jsonPath("$.length()").value(1))
                .andReturn();
    }

    @Test
    public void getTitleById_Success() throws Exception {
        mockMvc.perform(get("/titles/title/" + savedTitle.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedTitle.getName()))
                .andExpect(jsonPath("$.departmentId").value(savedDepartment.getId().toString()));
    }

    @Test
    public void getTitleById_NotFound() throws Exception {
        mockMvc.perform(get("/titles/title/" + UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllDepartments_Successful() throws Exception {
        mockMvc.perform(get("/titles/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value(savedDepartment.getName()))
                .andExpect(jsonPath("$[1].id").value(savedDepartment.getId().toString()));
    }

    @Test
    public void getAllDepartments_Not_Successful() throws Exception {
        titleRepository.delete(savedTitle);
        departmentRepository.delete(savedDepartment);
        mockMvc.perform(get("/titles/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                //Returns 1 because we have a manual entry inside if not it will be 0 and an empty list
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void getAllByDepartment_Successful() throws Exception {
        mockMvc.perform(get("/titles/" + savedDepartment.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value(savedTitle.getName()));
    }

    @Test
    public void getAllByDepartment_Not_Successful() throws Exception {
        mockMvc.perform(get("/titles/NonExistentDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void getById_Successful() throws Exception {
        mockMvc.perform(get("/titles/" + savedDepartment.getName() + "/" + savedTitle.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedTitle.getName()))
                .andExpect(jsonPath("$.departmentId").value(savedDepartment.getId().toString()));
    }

    @Test
    public void getById_Not_Successful() throws Exception {
        mockMvc.perform(get("/titles/InvalidDepartment/InvalidTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTitle_Successful() throws Exception {
        TitleRequestDTO newTitle = new TitleRequestDTO();
        newTitle.setName("New Title");
        newTitle.setDepartmentId(savedDepartment.getId());
        MvcResult result = mockMvc.perform(post("/titles/newTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTitle))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newTitle.getName()))
                .andExpect(jsonPath("$.departmentId").value(newTitle.getDepartmentId().toString()))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        String createdTitleId = JsonPath.parse(responseBody).read("$.id").toString();
        titleRepository.deleteById(UUID.fromString(createdTitleId));
    }

    @Test
    public void createTitle_Not_Successful_InValid_Department() throws Exception {
        TitleRequestDTO invalidTitle = new TitleRequestDTO();
        invalidTitle.setName("Invalid Title");
        invalidTitle.setDepartmentId(UUID.randomUUID());

        mockMvc.perform(post("/titles/newTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTitle))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTitle_Not_Successful_AlreadyExist() throws Exception {
        TitleRequestDTO duplicateTitle = new TitleRequestDTO();
        duplicateTitle.setName(savedTitle.getName());
        duplicateTitle.setDepartmentId(savedDepartment.getId());

        mockMvc.perform(post("/titles/newTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateTitle))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createDepartment_Successful() throws Exception {
        DepartmentRequestDTO newDept = new DepartmentRequestDTO();
        newDept.setName("New Department");

        MvcResult result=mockMvc.perform(post("/titles/newDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDept))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        String createdTitleId = JsonPath.parse(responseBody).read("$.id").toString();
        departmentRepository.deleteById(UUID.fromString(createdTitleId));
    }

    @Test
    public void createDepartment_Not_Successful_AlreadyExists() throws Exception {

        mockMvc.perform(post("/titles/newDepartment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequestDTO))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTitle_Successful() throws Exception {
        mockMvc.perform(delete("/titles/" + savedDepartment.getName() + "/" + savedTitle.getName())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteTitle_Not_Successful() throws Exception {
        mockMvc.perform(delete("/titles/NonExistentDept/NonExistentTitle")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }
}
