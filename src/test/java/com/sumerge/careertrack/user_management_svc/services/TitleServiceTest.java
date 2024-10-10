package com.sumerge.careertrack.user_management_svc.services;

import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.*;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;
import com.sumerge.careertrack.user_management_svc.services.TitleService;

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
    void getAllTitles_Successful() {
        Title title1 = new Title();
        Title title2 = new Title();

        List<Title> expectedTitles = Arrays.asList(title1, title2);

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
    void getAllTitles_Not_Successful() {
        when(titleRepository.findAll()).thenReturn(Collections.emptyList());
        List<TitleResponseDTO> receivedTitles = titleService.getAllTitles();
        assertEquals(receivedTitles.size(), 0);
        verify(titleRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_Successful() {
        Department dept1 = new Department();
        Department dept2 = new Department();

        List<Department> expectedDepartments = Arrays.asList(dept1, dept2);

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
    void getAllDepartments_Not_Successful() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        List<DepartmentResponseDTO> receivedDepartments = titleService.getAllDepartments();
        assertEquals(receivedDepartments.size(), 0);
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void findByDept_Successful() {
        String departmentName = "Engineering";
        Department department = Department.builder().name(departmentName).build();

        Title title1 = Title.builder().id(UUID.randomUUID()).department(department).name("Software Engineer")
                .isManager(false).build();
        Title title2 = Title.builder().id(UUID.randomUUID()).department(department).name("Senior Engineer")
                .isManager(true).build();

        List<Title> titles = Arrays.asList(title1, title2);

        when(titleRepository.findByDepartmentName(departmentName)).thenReturn(titles);

        TitleResponseDTO dto1 = new TitleResponseDTO(UUID.randomUUID(), UUID.randomUUID(), "Software Engineer", false);
        TitleResponseDTO dto2 = new TitleResponseDTO(UUID.randomUUID(), UUID.randomUUID(), "Senior Engineer", true);

        when(titleMapper.toDTO(title1)).thenReturn(dto1);
        when(titleMapper.toDTO(title2)).thenReturn(dto2);

        List<TitleResponseDTO> result = titleService.findByDept(departmentName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Software Engineer", result.get(0).getName());
        assertEquals("Senior Engineer", result.get(1).getName());

        verify(titleRepository).findByDepartmentName(departmentName);
        verify(titleMapper).toDTO(title1);
        verify(titleMapper).toDTO(title2);
    }

    @Test
    void findByDept_Not_Successful() {
        String departmentName = "Not Engineering";
        when(titleRepository.findByDepartmentName(departmentName)).thenReturn(Collections.emptyList());
        List<TitleResponseDTO> receivedTitles = titleService.findByDept(departmentName);
        assertEquals(receivedTitles.size(), 0);
        verify(titleRepository, times(1)).findByDepartmentName(departmentName);
    }

    @Test
    void createTitle_Successful() {
        TitleRequestDTO requestDTO = new TitleRequestDTO();
        requestDTO.setDepartmentId(UUID.randomUUID());
        requestDTO.setName("New Title");

        Title title = new Title();
        title.setName("New Title");

        Department department = new Department();
        department.setId(requestDTO.getDepartmentId());
        department.setName("HR");

        Title savedTitle = new Title();
        savedTitle.setName("New Title");
        savedTitle.setDepartment(department);

        TitleResponseDTO responseDTO = new TitleResponseDTO();
        responseDTO.setName("New Title");

        // Mock behavior
        when(titleMapper.toTitle(requestDTO)).thenReturn(title);
        when(departmentRepository.findById(requestDTO.getDepartmentId())).thenReturn(Optional.of(department));
        when(titleRepository.existsByNameAndDepartmentName(requestDTO.getName(), department.getName()))
                .thenReturn(false);
        when(titleRepository.save(title)).thenReturn(savedTitle);
        when(titleMapper.toDTO(savedTitle)).thenReturn(responseDTO);

        TitleResponseDTO result = titleService.createTitle(requestDTO);

        assertNotNull(result);
        assertEquals("New Title", result.getName());
        verify(departmentRepository, times(1)).findById(requestDTO.getDepartmentId());
        verify(titleRepository, times(1)).existsByNameAndDepartmentName(requestDTO.getName(), department.getName());
        verify(titleRepository, times(1)).save(title);

    }

    @Test
    void createTitle_Not_Successful_DepartmentNotFound() {
        TitleRequestDTO requestDTO = new TitleRequestDTO();
        UUID departmentId = UUID.randomUUID();
        requestDTO.setDepartmentId(departmentId);
        requestDTO.setName("New Title");
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
        assertThrows(DoesNotExistException.class, () -> titleService.createTitle(requestDTO));

        verify(titleRepository, times(0)).save(any());
    }

    @Test
    void createTitle_Not_Successful_TitleAlreadyExists() {
        TitleRequestDTO requestDTO = new TitleRequestDTO();
        UUID departmentId = UUID.randomUUID();
        requestDTO.setDepartmentId(departmentId);
        requestDTO.setName("Existing Title");

        Department department = new Department();
        department.setId(departmentId);
        department.setName("HR");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(titleRepository.existsByNameAndDepartmentName(requestDTO.getName(), department.getName()))
                .thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> titleService.createTitle(requestDTO));

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(titleRepository, times(1)).existsByNameAndDepartmentName(requestDTO.getName(), department.getName());
        verify(titleRepository, times(0)).save(any());
    }

    @Test
    void createDepartment_Successful() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Finance");

        Department department = Department.builder().name("Finance").build();

        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setName("Finance");

        when(departmentRepository.existsByName(requestDTO.getName())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toDTO(department)).thenReturn(responseDTO);

        DepartmentResponseDTO result = titleService.createDepartment(requestDTO);

        assertNotNull(result);
        assertEquals("Finance", result.getName());
        verify(departmentRepository, times(1)).existsByName(requestDTO.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
        verify(departmentMapper, times(1)).toDTO(department);
    }

    @Test
    void createDepartment_Not_Successful_AlreadyExists() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Finance");

        when(departmentRepository.existsByName(requestDTO.getName())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> titleService.createDepartment(requestDTO));

        verify(departmentRepository, times(1)).existsByName(requestDTO.getName());
        verify(departmentRepository, times(0)).save(any(Department.class));
        verify(departmentMapper, times(0)).toDTO(any(Department.class));
    }

    @Test
    void findByDepartmentAndTitle_Successful() {
        String departmentName = "HR";
        String titleName = "Manager";

        Title title = new Title();
        title.setName(titleName);

        TitleResponseDTO responseDTO = new TitleResponseDTO();
        responseDTO.setName(titleName);

        when(titleRepository.findByNameAndDepartmentName(titleName, departmentName))
                .thenReturn(Optional.of(title));
        when(titleMapper.toDTO(title)).thenReturn(responseDTO);

        TitleResponseDTO result = titleService.findByDepartmentAndTitle(departmentName, titleName);

        assertNotNull(result);
        assertEquals(titleName, result.getName());
        verify(titleRepository, times(1)).findByNameAndDepartmentName(titleName, departmentName);
        verify(titleMapper, times(1)).toDTO(title);
    }

    @Test
    void findByDepartmentAndTitle_Not_Successful_NotFound() {
        String departmentName = "HR";
        String titleName = "Manager";

        when(titleRepository.findByNameAndDepartmentName(titleName, departmentName))
                .thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class,
                () -> titleService.findByDepartmentAndTitle(departmentName, titleName));

        verify(titleRepository, times(1)).findByNameAndDepartmentName(titleName, departmentName);
        verify(titleMapper, times(0)).toDTO(any());
    }

    @Test
    void deleteTitle_Successful() {
        String departmentName = "HR";
        String titleName = "Manager";

        Title title = new Title();
        title.setName(titleName);
        when(titleRepository.findByNameAndDepartmentName(titleName, departmentName))
                .thenReturn(Optional.of(title));

        titleService.deleteTitle(departmentName, titleName);

        verify(titleRepository, times(1)).findByNameAndDepartmentName(titleName, departmentName);
        verify(titleRepository, times(1)).delete(title);
    }

    @Test
    void deleteTitle_Not_Successful() {
        String departmentName = "HR";
        String titleName = "Manager";

        when(titleRepository.findByNameAndDepartmentName(titleName, departmentName))
                .thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class,
                () -> titleService.findByDepartmentAndTitle(departmentName, titleName));

        verify(titleRepository, times(1)).findByNameAndDepartmentName(titleName, departmentName);
    }
}