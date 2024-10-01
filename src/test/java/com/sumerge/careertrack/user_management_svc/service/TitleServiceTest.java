package com.sumerge.careertrack.user_management_svc.service;

import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentMapper;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentResponseDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitleServiceTest {

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private TitleMapper titleMapper;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private TitleService titleService;

    @Test
    void getAllTitles_Successful(){
        Title title1 = new Title();
        Title title2 = new Title();

        List<Title> expectedTitles = Arrays.asList(title1,title2);

        when(titleRepository.findAll()).thenReturn(expectedTitles);
        List<TitleResponseDTO> receivedTitles = titleService.getAllTitles();

        assertNotNull(receivedTitles);
        assertEquals(2, receivedTitles.size());
        assertEquals(expectedTitles.stream()
                .map(titleMapper::toDTO)
                .collect(Collectors.toList()), receivedTitles);

        verify(titleRepository, times(1)).findAll();
    }

    @Test
    void getAllTitles_Not_Successful(){
        when(titleRepository.findAll()).thenReturn(Collections.emptyList());
        List<TitleResponseDTO> receivedTitles = titleService.getAllTitles();
        assertEquals(receivedTitles.size(), 0);
        verify(titleRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_Successful(){
        Department dept1 = new Department();
        Department dept2 = new Department();

        List<Department> expectedDepartments = Arrays.asList(dept1,dept2);

        when(departmentRepository.findAll()).thenReturn(expectedDepartments);
        List<DepartmentResponseDTO> receivedDepartments = titleService.getAllDepartments();

        assertNotNull(receivedDepartments);
        assertEquals(2, receivedDepartments.size());
        assertEquals(expectedDepartments.stream()
                .map(departmentMapper::toDTO)
                .collect(Collectors.toList()), receivedDepartments);

        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_Not_Successful(){
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        List<DepartmentResponseDTO> receivedDepartments = titleService.getAllDepartments();
        assertEquals(receivedDepartments.size(), 0);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void findByDept_Successful() {
        String departmentName = "Engineering";
        Department department = Department.builder().name(departmentName).build();

        Title title1 = Title.builder().id(UUID.randomUUID()).department(department).name("Software Engineer").isManager(false).build();
        Title title2 = Title.builder().id(UUID.randomUUID()).department(department).name("Senior Engineer").isManager(true).build();

        List<Title> titles = Arrays.asList(title1, title2);

        when(titleRepository.findByDepartmentName(departmentName)).thenReturn(titles);


        TitleResponseDTO dto1 = new TitleResponseDTO(UUID.randomUUID(), UUID.randomUUID(),"Software Engineer", false);
        TitleResponseDTO dto2 = new TitleResponseDTO(UUID.randomUUID(), UUID.randomUUID(),"Senior Engineer", true);

        when(titleMapper.toDTO(title1)).thenReturn(dto1);
        when(titleMapper.toDTO(title2)).thenReturn(dto2);

        List<TitleResponseDTO> result = titleService.findByDept(departmentName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getTitleName());
        assertEquals("Senior Engineer", result.get(1).getTitleName());

        verify(titleRepository).findByDepartmentName(departmentName);
        verify(titleMapper).toDTO(title1);
        verify(titleMapper).toDTO(title2);
    }

}