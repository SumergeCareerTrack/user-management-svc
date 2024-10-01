package com.sumerge.careertrack.user_management_svc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentResponseDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import com.sumerge.careertrack.user_management_svc.service.TitleService;
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
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TitleController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TitleController.class)

@ComponentScan("com.sumerge.exceptions")
class TitleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TitleService titleService;

    @MockBean
    private TitleRepository titleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Title title;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        title = new Title();
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
    void deleteTitle_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";

        doNothing().when(titleService).deleteTitle(deptName,titleName);

        mockMvc.perform(delete("/titles/{deptName}/{titleName}", deptName, titleName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(titleService , times(1)).deleteTitle(deptName , titleName);
    }

    //TODO Review why it only works with the title repository
    @Test
    void deleteTitle_Not_Successful() throws Exception {
        String deptName = "HR";
        String titleName = "Hr Manager";

        when(titleRepository.findByNameAndDepartmentName(titleName, deptName)).thenThrow(DoesNotExistException.class);

        mockMvc.perform(delete("/titles/{deptName}/{titleName}", deptName, titleName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(titleService , times(1)).deleteTitle(deptName , titleName);
    }
}