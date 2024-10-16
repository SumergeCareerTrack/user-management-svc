package com.sumerge.careertrack.user_management_svc.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserMapper;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserResponseDTO;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@Service
public class AppUserService {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    TitleRepository titlesRepository;

    @Autowired
    DepartmentRepository deptRepository;

    @Autowired
    AppUserMapper userMapper;
    @Autowired
    private  PasswordEncoder passwordEncoder;


    public Page<AppUserResponseDTO> getAll(Pageable pageable) {
        Page<AppUser> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userMapper::toResponseDTO);
    }

    public List<AppUserResponseDTO> getBatch(List<UUID> ids) {
        return ids.stream()
                .map(userRepository::findById)
                .map(user -> user.orElseThrow(() -> new DoesNotExistException(
                        "One of the supplied IDs does not exist.")))
                .map(userMapper::toResponseDTO)
                .toList();
    }

    public AppUserResponseDTO getById(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, userId));
        return userMapper.toResponseDTO(user);
    }

    public AppUserResponseDTO getByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_EMAIL, email));
        return userMapper.toResponseDTO(user);
    }

    public List<AppUserResponseDTO> getManagersByDept(String deptName) {
        List<Title> titles = titlesRepository.findByDepartmentName(deptName);
        List<AppUser> managers = titles.stream()
                .filter(title -> title.isManager())
                .flatMap(title -> {
                    List<AppUser> users = userRepository.findByTitle(title);
                    return users.stream();
                })
                .collect(Collectors.toList());

        return managers.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<AppUserResponseDTO> getSubordinates(UUID managerId) {
        AppUser manager = userRepository.findById(managerId)
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, managerId));

        List<AppUser> users = userRepository.findAllByManager(manager);
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<AppUserResponseDTO> getAllByTitle(String titleName) {
        List<AppUser> users = userRepository.findByTitleName(titleName);
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public AppUserResponseDTO getManager(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, userId));

        AppUser userManager = user.getManager(); // TODO test if user has no manager
        return userMapper.toResponseDTO(userManager);
    }

    public AppUserResponseDTO createUser(AppUserRequestDTO userDTO) {
        AppUser userObj = userMapper.toAppUser(userDTO);
        boolean userExists = userRepository.existsByEmail(userObj.getEmail());

        if (userExists) {
            throw new AlreadyExistsException(AlreadyExistsException.APP_USER_EMAIL,
                    userObj.getEmail());
        }

        AppUser savedUser = userRepository.save(userObj);
        return userMapper.toResponseDTO(savedUser);
    }

    public AppUserResponseDTO updateUser(AppUserRequestDTO dto) {


        AppUser userObj = userRepository.findById(dto.getId())
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, dto.getId()));

        if (dto.getManagerId() != null) {
            AppUser manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new DoesNotExistException(
                            DoesNotExistException.APP_USER_ID, dto.getManagerId()));
            userObj.setManager(manager);
        }

        if (dto.getTitleId() != null) {
            Title title = titlesRepository.findById(dto.getTitleId())
                    .orElseThrow(() -> new DoesNotExistException(
                            DoesNotExistException.TITLE, dto.getTitleId()));
            userObj.setTitle(title);
            userObj.setDepartment(title.getDepartment());
        }

        if (dto.getEmail() != null) {
            userObj.setEmail(dto.getEmail());
        }

        if (dto.getFirstName() != null) {
            userObj.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            userObj.setLastName(dto.getLastName());
        }

        AppUser updatedUser = userRepository.save(userObj);
        return userMapper.toResponseDTO(updatedUser);
    }


    public void deleteUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, userId));
        userRepository.delete(user);
    }
        //TODO DO ITS TESTS
    public AppUserResponseDTO changePassword(String password, String userId) {
        AppUser user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new DoesNotExistException(
                        DoesNotExistException.APP_USER_ID, UUID.fromString(userId)));
        user.setPassword(passwordEncoder.encode(password));
        AppUser updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

}
