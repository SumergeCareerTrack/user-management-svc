package com.sumerge.careertrack.user_management_svc.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserResponseDTO;
import com.sumerge.careertrack.user_management_svc.service.AppUserService;

@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserService userService;

    /* GET METHODS */
    @GetMapping("/")
    public ResponseEntity<List<AppUserResponseDTO>> getAll() {
        List<AppUserResponseDTO> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers") // TODO review naming
    public ResponseEntity<List<AppUserResponseDTO>> getManagersByDept(@RequestBody String departmentName) {
        List<AppUserResponseDTO> users = userService.getManagersByDept(departmentName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AppUserResponseDTO> getById(@PathVariable UUID userId) {
        AppUserResponseDTO user = userService.getById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}") // TODO Review RequestBody vs PathVariable
    public ResponseEntity<AppUserResponseDTO> getByEmail(@PathVariable String email) {
        AppUserResponseDTO user = userService.getByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/title/{titleName}")
    public ResponseEntity<List<AppUserResponseDTO>> getAllByTitle(@PathVariable String titleName) {
        List<AppUserResponseDTO> users = userService.getAllByTitle(titleName);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}/subordinates")
    public ResponseEntity<List<AppUserResponseDTO>> getSubordinates(@PathVariable UUID userId) {
        List<AppUserResponseDTO> subordinates = userService.getSubordinates(userId);
        return ResponseEntity.ok(subordinates);
    }

    /* UPDATE METHODS */
    @PutMapping("/")
    public ResponseEntity<AppUserResponseDTO> updateUser(@RequestBody AppUserRequestDTO updatedDTO) {
        AppUserResponseDTO updatedUser = userService.updateUser(updatedDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /* DELETE METHODS */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}
