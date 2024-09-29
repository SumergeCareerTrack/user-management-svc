package com.sumerge.careertrack.user_management_svc.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sumerge.careertrack.user_management_svc.mappers.AppUserDTO;
import com.sumerge.careertrack.user_management_svc.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /* GET METHODS */
    @GetMapping("/")
    public ResponseEntity<List<AppUserDTO>> getAll() {
        List<AppUserDTO> users = userService.getAll();
        return ResponseEntity.ok(users);
    }
    

    @GetMapping("/{email}") // TODO Review RequestBody vs PathVariable
    public ResponseEntity<AppUserDTO> getByEmail(@PathVariable String email) {
        AppUserDTO user = userService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{titleName}")
    public ResponseEntity<List<AppUserDTO>> getAllByTitle(@PathVariable String titleName) {
        List<AppUserDTO> users = userService.getAllByTitle(titleName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers") // TODO review naming
    public ResponseEntity<List<AppUserDTO>> getManagersByDept(@RequestBody String departmentName) {
        List<AppUserDTO> users = userService.getManagersByDept(departmentName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUserDTO> getById(@PathVariable UUID userId) {
        AppUserDTO user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/manager") // TODO review need for extra endpoint
    public ResponseEntity<AppUserDTO> getManager(@PathVariable UUID userId) {
        AppUserDTO manager = userService.getManager(userId);
        return ResponseEntity.ok(manager);
    }

    @GetMapping("/{managerId}")
    public ResponseEntity<List<AppUserDTO>> getSubordinates(@PathVariable UUID managerId) {
        List<AppUserDTO> subordinates = userService.getSubordinates(managerId);
        return ResponseEntity.ok(subordinates);
    }

    /* POST METHODS */
    @PostMapping("/")
    public ResponseEntity<AppUserDTO> createUser(@RequestBody AppUserDTO user) {
        AppUserDTO newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    /* UPDATE METHODS */
    @PutMapping("/")
    public ResponseEntity<AppUserDTO> updateUser(@RequestBody AppUserDTO updatedDTO) {
        AppUserDTO updatedUser = userService.updateUser(updatedDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /* DELETE METHODS */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}
