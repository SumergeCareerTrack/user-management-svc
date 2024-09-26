package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TitlesRepository titlesRepository;

    @Autowired
    UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll()
        .orElseThrow(() -> new UserDoesNotExistException("No Users Found"));
        return users.stream().map(userMapper::mapToUserDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        return userMapper.mapToUserDTO(user);
    }
    
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with email %d does not exist", user.getEmail())));
        return userMapper.mapToUserDTO(user);
    }

    public List<UserDTO> getUsersByDep(UUID depId) {
        List<User> users = userRepository.findByDepId(depId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("Users in the department iwht ID %d does not exist", depId)));
        return users.stream().map(userMapper::mapToUserDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByManager(UUID managerId) {
        List<User> users = userRepository.findByManagerId(managerId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("Manager with ID %d doesn't manage any employees", managerId)));
        return users.stream().map(userMapper::mapToUserDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByTite(string title) {
        List<User> users = userRepository.findByManagerId(title)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("Users with Title %d doesn't exist", title)));
        return users.stream().map(userMapper::mapToUserDTO).collect(Collectors.toList());
    }

    public UserDTO getManager(UUID userId){
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        User manager = userRepository.findById(user.getManagerId())
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        return userMapper.mapToUserDTO(manager);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.mapToUser(userDTO);
        boolean userExists = userRepository.findByEmail(user.getEmail()).orElse(false);
        if(userExists) {
            throw new RuntimeException("User already exists");
        }
        
        User savedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(savedUser);
    }

    public UserDTO updateUser(UUID userId,UserDTO userDTO){
        User user = userMapper.mapToCourse(userDTO);
        userRepository.existsById(userId)
            .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        user.setId(userId);
        User updatedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(updatedUser);
    }

    public UserDTO promoteUser(UUID userId,string newTitle){
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        Title title = titlesRepository.findTitle(newTitle);
        if(title.getDepId() != user.getDepId()){
            throw new RuntimeException(String.format("Title %d doesn't match User's Department", newTitle));
        }
        user.setTitle(title);
        User updatedUser = userRepository.save(user);
        return userMapper.mapToUserDTO(updatedUser);
    }

    public void deleteUser(UUID userId){
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
        userRepository.delete(user);
    }

    // public UserDTO changeManager(UUID userId,UUID newManagerId){
    //     User user = userRepository.findById(userId)
    //     .orElseThrow(() -> new UserDoesNotExistException(String.format("User with ID %d does not exist", user.getId())));
    //     user.setManagerId(newManagerId);
    //     User updatedUser = userRepository.save(user);
    //     return userMapper.mapToUserDTO(updatedUser);
    // }



}
