package com.sumerge.careertrack.user_management_svc.controller;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sumerge.careertrack.user_management_svc.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* GET METHODS */
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users =  userService.getAllUsers();
        return ResponseEntity.ok(courses);
    }
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserById(@RequestBody UUID userId){
        UserDTO user =  userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestBody String email){
        UserDTO user =  userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsersByDep(@RequestBody String departmentName){
        List<UserDTO> user =  userService.getUsersByDep(departmentName);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsersByManager(@RequestBody UUID managerId){
        List<UserDTO> user =  userService.getUsersByManager(managerId);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsersByTite(@RequestBody String title){
        List<UserDTO> user =  userService.getUsersByTite(title);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/user/manager")
    public ResponseEntity<UserDTO> getManager(@RequestBody UUID userId){
        List<UserDTO> user =  userService.getManager(userId);
        return ResponseEntity.ok(user);
    }

    /* CREATE METHODS */
    @PostMapping("/registerUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO user){
        UserDTO newUser= userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    /* UPDATE METHODS */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/updateUser")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UUID userId,UserDTO updatedDTO){
        UserDTO updatedUser= userService.updateUser(userId,updatedDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/user/promoteUser")
    // public ResponseEntity<UserDTO> promoteUser(@RequestBody UUID userId,String title){
    //     UserDTO updatedUser= userService.promoteUser(userId,title);
    //     return ResponseEntity.ok(updatedUser);
    // }

    // @PreAuthorize("hasRole('ADMIN')")
    // @PutMapping("/user/changeManager")
    // public ResponseEntity<UserDTO> changeManager(@RequestBody UUID userId,UUID newManagerId){
    //     UserDTO updatedUser= userService.changeManager(userId,newManagerId);
    //     return ResponseEntity.ok(updatedUser);
    // }

    /* DELETE METHODS */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@RequestBody UUID userId){
        userService.deleteUser(userId);
    }

    
    
    

    
    


}
