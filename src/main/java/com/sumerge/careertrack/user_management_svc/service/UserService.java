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
import com.sumerge.careertrack.user_management_svc.mappers.AppUserDTO;
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

    public List<AppUserDTO> getAll() {
        List<AppUser> users = userRepository.findAll();
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public AppUserDTO getById(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));
        return userMapper.toDTO(user);
    }

    public AppUserDTO getByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with email %d does not exist", email)));
        return userMapper.toDTO(user);
    }

    public List<AppUserDTO> getManagersByDept(String deptName) {
        List<Title> titles = titlesRepository.findByIdDepartment(deptName);
        List<AppUser> managers = titles.stream()
                .filter(title -> title.isManager())
                .flatMap(title -> userRepository.findAllByTitleId(title.getId()).stream())
                .collect(Collectors.toList());
        return managers.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public List<AppUserDTO> getSubordinates(UUID managerId) {
        AppUser manager = userRepository.findById(managerId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("Manager with ID %d doesn't manage any employees", managerId)));

        List<AppUser> users = userRepository.findAllByManager(manager);
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public List<AppUserDTO> getAllByTitle(String titleName) {
        List<AppUser> users = userRepository.findByTitleIdName(titleName);
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    public AppUserDTO getManager(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));

        AppUser userManager = user.getManager(); // TODO test if user has no manager
        return userMapper.toDTO(userManager);
    }

    public AppUserDTO createUser(AppUserDTO userDTO) {
        AppUser userObj = userMapper.toAppUser(userDTO);
        boolean userExists = userRepository.existsByEmail(userObj.getEmail());

        if (userExists) {
            throw new AppUserAlreadyExistsException("User with email \"%f\" already exists.");
        }

        AppUser savedUser = userRepository.save(userObj);
        return userMapper.toDTO(savedUser);
    }

    public AppUserDTO updateUser(AppUserDTO userDTO) {
        AppUser userObj = userMapper.toAppUser(userDTO);
        userRepository.findById(userObj.getId())
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userObj.getId())));
        AppUser updatedUser = userRepository.save(userObj);
        return userMapper.toDTO(updatedUser);
    }

    public void deleteUser(UUID userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new AppUserDoesNotExistException(
                        String.format("AppUser with ID %d does not exist", userId)));
        userRepository.delete(user);
    }

}
