package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.AppUser;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AppUserAlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.AppUserDoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserMapper;
import com.sumerge.careertrack.user_management_svc.repositories.AppUserRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@Service
public class UserService {

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    TitleRepository titlesRepository;

    @Autowired
    AppUserMapper userMapper;

    public List<AppUserRequestDTO> getAll() {
        List<AppUser> users = userRepository.findAll();
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public AppUserRequestDTO getById(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));
        return userMapper.toResponseDTO(user);
    }

    public AppUserRequestDTO getByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with email %d does not exist", email)));
        return userMapper.toResponseDTO(user);
    }

    public List<AppUserRequestDTO> getManagersByDept(String deptName) {
        List<Title> titles = titlesRepository.findByIdDepartment(deptName);
        List<AppUser> managers = titles.stream()
                .filter(title -> title.isManager())
                .flatMap(title -> userRepository.findAllByTitleId(title.getId()).stream())
                .collect(Collectors.toList());
        return managers.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<AppUserRequestDTO> getSubordinates(UUID managerId) {
        AppUser manager = userRepository.findById(managerId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("Manager with ID %d doesn't manage any employees", managerId)));

        List<AppUser> users = userRepository.findAllByManager(manager);
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public List<AppUserRequestDTO> getAllByTitle(String titleName) {
        List<AppUser> users = userRepository.findByTitleIdName(titleName);
        return users.stream().map(userMapper::toResponseDTO).collect(Collectors.toList());
    }

    public AppUserRequestDTO getManager(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));

        AppUser userManager = user.getManager(); // TODO test if user has no manager
        return userMapper.toResponseDTO(userManager);
    }

    public AppUserRequestDTO createUser(AppUserRequestDTO userDTO) {
        AppUser userObj = userMapper.toAppUser(userDTO);
        boolean userExists = userRepository.existsByEmail(userObj.getEmail());

        if (userExists) {
            throw new AppUserAlreadyExistsException("User with email \"%f\" already exists.");
        }

        AppUser savedUser = userRepository.save(userObj);
        return userMapper.toResponseDTO(savedUser);
    }

    public AppUserRequestDTO updateUser(AppUserRequestDTO userDTO) {
        AppUser userObj = userMapper.toAppUser(userDTO);
        userRepository.findById(userObj.getId())
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userObj.getId())));
        AppUser updatedUser = userRepository.save(userObj);
        return userMapper.toResponseDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));
        userRepository.delete(user);
    }

}
