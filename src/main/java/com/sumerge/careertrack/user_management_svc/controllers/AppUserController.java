package com.sumerge.careertrack.user_management_svc.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumerge.careertrack.user_management_svc.mappers.AppUserRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.AppUserResponseDTO;
import com.sumerge.careertrack.user_management_svc.services.AppUserService;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class AppUserController {

    @Autowired
    private AppUserService userService;

    /* GET METHODS */
    @GetMapping("/")
    public ResponseEntity<List<AppUserResponseDTO>> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page == null || size == null || size == 0) {
            List<AppUserResponseDTO> allUsers = userService.getAll();
            return ResponseEntity.ok(allUsers);
        } else {
            Pageable pageable = PageRequest.of(page, size);
            Page<AppUserResponseDTO> usersPage = userService.getAll(pageable);
            List<AppUserResponseDTO> users = usersPage.getContent();
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping("/batch")
    public ResponseEntity<List<AppUserResponseDTO>> getBatch(@RequestBody List<UUID> ids) {
        List<AppUserResponseDTO> users = userService.getBatch(ids);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers") // TODO review naming
    public ResponseEntity<List<AppUserResponseDTO>> getManagersByDept(@RequestParam String departmentName) {
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
    //TODO DO ITS TESTS
    @PutMapping("/password/{userId}")
    public ResponseEntity<AppUserResponseDTO> changePassword(@RequestBody String password,@PathVariable String userId) {
        AppUserResponseDTO updatedUser = userService.changePassword(password, userId);
        return ResponseEntity.ok(updatedUser);
    }


    /* DELETE METHODS */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }

}
