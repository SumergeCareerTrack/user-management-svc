
package com.sumerge.careertrack.user_management_svc.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserMapper;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserResponseDTO;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

public class AppUserServiceTests {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private TitleRepository titleRepository;


    @Mock
    private AppUserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService userService;

    private AppUser mockUser;
    private AppUserResponseDTO mockUserResponseDTO;
    private AppUserRequestDTO mockUserRequestDTO;
    private Title mockTitle;
    private Department mockDepartment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize common objects
        mockUser = new AppUser();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail("test@example.com");

        mockUserResponseDTO = new AppUserResponseDTO();
        mockUserResponseDTO.setId(mockUser.getId());
        mockUserResponseDTO.setEmail(mockUser.getEmail());

        mockUserRequestDTO = new AppUserRequestDTO();
        mockUserRequestDTO.setId(mockUser.getId());
        mockUserRequestDTO.setEmail("test@example.com");

        mockTitle = new Title();
        mockTitle.setId(UUID.randomUUID());
        mockTitle.setName("Senior Engineer");

        mockDepartment = new Department();
        mockDepartment.setId(UUID.randomUUID());
        mockDepartment.setName("Engineering");
    }

    @Test
    void getAll_Successful() {
        List<AppUser> mockUsers = Arrays.asList(mockUser, mockUser);
        when(userRepository.findAll()).thenReturn(mockUsers);

        when(userMapper.toResponseDTO(any(AppUser.class))).thenReturn(mockUserResponseDTO);

        List<AppUserResponseDTO> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
        verify(userMapper, times(2)).toResponseDTO(any(AppUser.class));
    }
    @Test
    void getAll_Pageable_Successful() {
        Page<AppUser> mockPage = new PageImpl<>(Arrays.asList(mockUser, mockUser));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        PageRequest pageable = PageRequest.of(0, 2);
        Page<AppUserResponseDTO> result = userService.getAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void getBatch_Successful() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userRepository.findById(id1)).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(id2)).thenReturn(Optional.of(mockUser));

        when(userMapper.toResponseDTO(any(AppUser.class))).thenReturn(mockUserResponseDTO);

        List<AppUserResponseDTO> result = userService.getBatch(Arrays.asList(id1, id2));

        assertEquals(2, result.size());
        verify(userRepository).findById(id1);
        verify(userRepository).findById(id2);
        verify(userMapper, times(2)).toResponseDTO(any(AppUser.class));
    }

    @Test
    void getBatch_DoesNotExistException() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        when(userRepository.findById(id1)).thenReturn(Optional.of(new AppUser()));
        when(userRepository.findById(id2)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.getBatch(Arrays.asList(id1, id2)));

        verify(userRepository).findById(id1);
        verify(userRepository).findById(id2);

    }

    @Test
    void getById_Successful() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponseDTO(mockUser)).thenReturn(mockUserResponseDTO);

        AppUserResponseDTO result = userService.getById(userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDTO(mockUser);
    }

    @Test
    void getById_DoesNotExistException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.getById(userId));
    }

    @Test
    void getByEmail_Successful() {
        String email = "test@example.com";
        AppUser mockUser = this.mockUser;
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        AppUserResponseDTO mockDTO = new AppUserResponseDTO();
        when(userMapper.toResponseDTO(mockUser)).thenReturn(mockDTO);

        AppUserResponseDTO result = userService.getByEmail(email);

        assertNotNull(result);
        verify(userRepository).findByEmail(email);
        verify(userMapper).toResponseDTO(mockUser);
    }

    @Test
    void getByEmail_DoesNotExistException() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.getByEmail(email));
    }
    @Test
    void getManagersByDept_Successful() {
        String deptName = "Engineering";
        mockTitle.setManager(true);
        mockTitle.setDepartment(mockDepartment);

        AppUser manager = new AppUser();
        AppUserResponseDTO managerDto = new AppUserResponseDTO();

        when(titleRepository.findByDepartmentName(deptName)).thenReturn(Arrays.asList(mockTitle));
        when(userRepository.findByTitle(mockTitle)).thenReturn(Arrays.asList(manager));
        when(userMapper.toResponseDTO(manager)).thenReturn(managerDto);

        List<AppUserResponseDTO> result = userService.getManagersByDept(deptName);

        assertEquals(1, result.size());
        assertEquals(managerDto, result.get(0));
        verify(titleRepository).findByDepartmentName(deptName);
        verify(userRepository).findByTitle(mockTitle);
        verify(userMapper).toResponseDTO(manager);
    }

    @Test
    void getManagersByDept_NoManagersFound() {
        String deptName = "Sales";
        when(titleRepository.findByDepartmentName(deptName)).thenReturn(Arrays.asList());

        List<AppUserResponseDTO> result = userService.getManagersByDept(deptName);

        assertTrue(result.isEmpty());
        verify(titleRepository).findByDepartmentName(deptName);
        verify(userRepository, never()).findByTitle(any(Title.class));
        verify(userMapper, never()).toResponseDTO(any(AppUser.class));
    }
    @Test
    void getSubordinates_Successful() {
        UUID managerId = UUID.randomUUID();
        AppUser manager = new AppUser();
        AppUser subordinate1 = new AppUser();
        subordinate1.setManager(manager);
        AppUser subordinate2 = new AppUser();
        subordinate2.setManager(manager);

        AppUserResponseDTO dto1 = new AppUserResponseDTO();
        AppUserResponseDTO dto2 = new AppUserResponseDTO();

        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(userRepository.findAllByManager(manager)).thenReturn(Arrays.asList(subordinate1, subordinate2));
        when(userMapper.toResponseDTO(subordinate1)).thenReturn(dto1);
        when(userMapper.toResponseDTO(subordinate2)).thenReturn(dto2);

        List<AppUserResponseDTO> result = userService.getSubordinates(managerId);

        assertEquals(2, result.size());
        verify(userRepository).findById(managerId);
        verify(userRepository).findAllByManager(manager);
        verify(userMapper, times(2)).toResponseDTO(any(AppUser.class));
    }
    @Test
    void getSubordinates_ManagerNotFound() {
        UUID managerId = UUID.randomUUID();
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.getSubordinates(managerId));

        verify(userRepository).findById(managerId);
        verify(userRepository, never()).findAllByManager(any(AppUser.class));
    }
    //TODO Ask Youssef 3ady a3mel hagat empty we a test byha enaha gets returned wala la2
    @Test
    void getAllByTitle_Successful() {
        String titleName = "Senior Engineer";
        AppUser user1 = new AppUser();
        AppUser user2 = new AppUser();

        AppUserResponseDTO dto1 = new AppUserResponseDTO();
        AppUserResponseDTO dto2 = new AppUserResponseDTO();

        when(userRepository.findByTitleName(titleName)).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toResponseDTO(user1)).thenReturn(dto1);
        when(userMapper.toResponseDTO(user2)).thenReturn(dto2);

        List<AppUserResponseDTO> result = userService.getAllByTitle(titleName);

        assertEquals(2, result.size());
        verify(userRepository).findByTitleName(titleName);
        verify(userMapper, times(2)).toResponseDTO(any(AppUser.class));
    }

    @Test
    void getAllByTitle_EmptyList() {
        String titleName = "Unknown Title";
        when(userRepository.findByTitleName(titleName)).thenReturn(Arrays.asList());

        List<AppUserResponseDTO> result = userService.getAllByTitle(titleName);

        assertTrue(result.isEmpty());
        verify(userRepository).findByTitleName(titleName);
        verify(userMapper, never()).toResponseDTO(any(AppUser.class));
    }

    @Test
    void createUser_AlreadyExistsException() {
        when(userMapper.toAppUser(mockUserRequestDTO)).thenReturn(mockUser);
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(mockUserRequestDTO));
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createUser_Successful() {
        when(userMapper.toAppUser(mockUserRequestDTO)).thenReturn(mockUser);
        when(userRepository.existsByEmail(mockUser.getEmail())).thenReturn(false);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.toResponseDTO(mockUser)).thenReturn(mockUserResponseDTO);

        AppUserResponseDTO result = userService.createUser(mockUserRequestDTO);

        assertNotNull(result);
        verify(userRepository).existsByEmail(mockUser.getEmail());
        verify(userRepository).save(mockUser);
        verify(userMapper).toResponseDTO(mockUser);
    }
    @Test
    void updateUser_Successful() {
        UUID userId = UUID.randomUUID();  // This should be the same across mocks
        UUID managerId = UUID.randomUUID();
        UUID titleId = UUID.randomUUID();



        mockUser.setId(userId);
        AppUser manager = new AppUser();
        manager.setId(managerId);

        mockTitle.setId(titleId);

        mockUserRequestDTO.setId(userId);
        mockUserRequestDTO.setManagerId(managerId);
        mockUserRequestDTO.setTitleId(titleId);

        // Stubbing the repository calls
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser)); // This is key
        when(userRepository.findById(managerId)).thenReturn(Optional.of(manager));
        when(titleRepository.findById(titleId)).thenReturn(Optional.of(mockTitle));
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.toResponseDTO(mockUser)).thenReturn(mockUserResponseDTO);

        // Calling the method under test
        AppUserResponseDTO result = userService.updateUser(mockUserRequestDTO);

        // Assertions
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).findById(managerId);
        verify(titleRepository).findById(titleId);
        verify(userRepository).save(mockUser);
        verify(userMapper).toResponseDTO(mockUser);
    }




    @Test
    void updateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        AppUserRequestDTO requestDTO = new AppUserRequestDTO();
        requestDTO.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.updateUser(requestDTO));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void updateUser_ManagerNotFound() {
        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();
        AppUserRequestDTO requestDTO = new AppUserRequestDTO();
        requestDTO.setId(userId);
        requestDTO.setManagerId(managerId);

        AppUser user = new AppUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(managerId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.updateUser(requestDTO));
        verify(userRepository).findById(userId);
        verify(userRepository).findById(managerId);
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void updateUser_TitleNotFound() {
        UUID userId = UUID.randomUUID();
        UUID titleId = UUID.randomUUID();

        AppUserRequestDTO requestDTO = new AppUserRequestDTO();
        requestDTO.setId(userId);
        requestDTO.setTitleId(titleId);

        AppUser user = new AppUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(titleRepository.findById(titleId)).thenReturn(Optional.empty());
        assertThrows(DoesNotExistException.class, () -> userService.updateUser(requestDTO));

        verify(userRepository).findById(userId);
        verify(titleRepository).findById(titleId);
        verify(userRepository, never()).save(any(AppUser.class));
    }

    @Test
    void deleteUser_Successful() {
        UUID userId = UUID.randomUUID();
        AppUser mockUser = new AppUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(mockUser);
    }

    @Test
    void deleteUser_DoesNotExistException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).delete(any(AppUser.class));
    }

    @Test
    void changePassword_Successful() {
        UUID userId = UUID.randomUUID();
        String newPassword = "newPassword";
        AppUser mockUser = new AppUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        userService.changePassword(newPassword, userId.toString());

        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(mockUser);
    }

    @Test
    void changePassword_DoesNotExistException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DoesNotExistException.class, () -> userService.changePassword("newPassword", userId.toString()));
    }
}
