package com.sumerge.careertrack.user_management_svc.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumerge.careertrack.user_management_svc.controllers.TitleController;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentResponseDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.services.TitleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TitleController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TitleController.class)
@ComponentScan("com.sumerge.careertrack.user_management_svc.exceptions")
class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTitles_Successful() throws Exception {
        TitleResponseDTO titleResponseDTO = new TitleResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleResponseDTO.setId(expectedUUID);
        List<TitleResponseDTO> titles = new ArrayList<>();
        titles.add(titleResponseDTO);
        when(titleService.getAllTitles()).thenReturn(titles);
        mockMvc.perform(get("/titles/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(expectedUUID.toString()));
        verify(titleService, times(1)).getAllTitles();
    }

    @Test
    void getAllTitles_Not_Successful() throws Exception {
        List<TitleResponseDTO> titles = new ArrayList<>();
        when(titleService.getAllTitles()).thenReturn(titles);
        mockMvc.perform(get("/titles/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(titleService, times(1)).getAllTitles();
    }

    @Test
    void getAllDepartments_Successful() throws Exception {
        DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        departmentResponseDTO.setId(expectedUUID);
        List<DepartmentResponseDTO> deps = new ArrayList<>();
        deps.add(departmentResponseDTO);
        when(titleService.getAllDepartments()).thenReturn(deps);
        mockMvc.perform(get("/titles/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(expectedUUID.toString()));
        verify(titleService, times(1)).getAllDepartments();
    }

    @Test
    void getAllDepartments_Not_Successful() throws Exception {
        List<DepartmentResponseDTO> deps = new ArrayList<>();
        when(titleService.getAllDepartments()).thenReturn(deps);
        mockMvc.perform(get("/titles/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        verify(titleService, times(1)).getAllDepartments();
    }

    @Test
    void getAllByDepartment_Successful() throws Exception {
        String deptName = "HR";

        TitleResponseDTO titleResponseDTO = new TitleResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleResponseDTO.setId(expectedUUID);
        List<TitleResponseDTO> titles = new ArrayList<>();
        titles.add(titleResponseDTO);
        when(titleService.findByDept(deptName)).thenReturn(titles);
        mockMvc.perform(get("/titles/{deptName}", deptName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(expectedUUID.toString()));

        verify(titleService, times(1)).findByDept(deptName);
    }

    @Test
    void getAllByDepartment_Not_Successful() throws Exception {
        String deptName = "HR";
        List<TitleResponseDTO> titles = new ArrayList<>();
        when(titleService.findByDept(deptName)).thenReturn(titles);
        mockMvc.perform(get("/titles/{deptName}", deptName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(titleService, times(1)).findByDept(deptName);
    }

    @Test
    void getById_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";
        TitleResponseDTO titleResponseDTO = new TitleResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleResponseDTO.setId(expectedUUID);
        titleResponseDTO.setName(titleName);
        when(titleService.findByDepartmentAndTitle(deptName, titleName))
                .thenReturn(titleResponseDTO);

        mockMvc.perform(get("/titles/{deptName}/{titleName}", deptName, titleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUUID.toString()))
                .andExpect(jsonPath("$.name").value(titleName));

        verify(titleService, times(1)).findByDepartmentAndTitle(deptName, titleName);
    }

    @Test
    void getById_Not_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";

        when(titleService.findByDepartmentAndTitle(deptName, titleName)).thenThrow(DoesNotExistException.class);
        mockMvc.perform(get("/titles/{deptName}/{titleName}", deptName, titleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(titleService, times(1)).findByDepartmentAndTitle(deptName, titleName);

    }

    @Test
    void createTitle_Successful() throws Exception {
        TitleRequestDTO titleRequestDTO = new TitleRequestDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleRequestDTO.setDepartmentId(expectedUUID);
        titleRequestDTO.setName("HELLO");
        TitleResponseDTO titleResponseDTO = new TitleResponseDTO();
        titleResponseDTO.setId(expectedUUID);
        when(titleService.createTitle(titleRequestDTO)).thenReturn(titleResponseDTO);
        mockMvc.perform(post("/titles/newTitle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUUID.toString()));
        verify(titleService, times(1)).createTitle(titleRequestDTO);
    }

    @Test
    void createTitle_Not_Successful_InValid_Department() throws Exception {
        TitleRequestDTO titleRequestDTO = new TitleRequestDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleRequestDTO.setDepartmentId(expectedUUID);
        titleRequestDTO.setName("HELLO");

        when(titleService.createTitle(titleRequestDTO)).thenThrow(DoesNotExistException.class);

        mockMvc.perform(post("/titles/newTitle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(titleService, times(1)).createTitle(titleRequestDTO);
    }

    @Test
    void createTitle_Not_Successful_AlreadyExist() throws Exception {
        TitleRequestDTO titleRequestDTO = new TitleRequestDTO();
        UUID expectedUUID = UUID.randomUUID();
        titleRequestDTO.setDepartmentId(expectedUUID);
        titleRequestDTO.setName("HELLO");

        when(titleService.createTitle(titleRequestDTO)).thenThrow(AlreadyExistsException.class);

        mockMvc.perform(post("/titles/newTitle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titleRequestDTO)))
                .andExpect(status().isBadRequest());

        verify(titleService, times(1)).createTitle(titleRequestDTO);
    }

    @Test
    void createDepartment_Successful() throws Exception {
        DepartmentRequestDTO departmentRequestDTO = new DepartmentRequestDTO();
        DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        departmentResponseDTO.setId(expectedUUID);

        when(titleService.createDepartment(any(DepartmentRequestDTO.class))).thenReturn(departmentResponseDTO);
        mockMvc.perform(post("/titles/newDepartment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedUUID.toString()));
        verify(titleService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    void createDepartment_Not_Successful() throws Exception {
        DepartmentRequestDTO departmentRequestDTO = new DepartmentRequestDTO();
        DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
        UUID expectedUUID = UUID.randomUUID();
        departmentResponseDTO.setId(expectedUUID);

        when(titleService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenThrow(AlreadyExistsException.class);
        mockMvc.perform(post("/titles/newDepartment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentRequestDTO)))
                .andExpect(status().isBadRequest());
        verify(titleService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    void deleteTitle_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";

        doNothing().when(titleService).deleteTitle(deptName, titleName);

        mockMvc.perform(delete("/titles/{deptName}/{titleName}", deptName, titleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(titleService, times(1)).deleteTitle(deptName, titleName);
    }

    @Test
    void deleteTitle_Not_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";

        when(titleService.findByDepartmentAndTitle(titleName, deptName)).thenThrow(DoesNotExistException.class);

        mockMvc.perform(delete("/titles/{deptName}/{titleName}", deptName, titleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(titleService, times(1)).deleteTitle(deptName, titleName);
    }
}