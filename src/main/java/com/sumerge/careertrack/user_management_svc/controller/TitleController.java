package com.sumerge.careertrack.user_management_svc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumerge.careertrack.user_management_svc.mappers.DepartmentRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentResponseDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.service.TitleService;

@CrossOrigin
@RestController
@RequestMapping("/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /* Get Mappings */
    @GetMapping("/")
    public ResponseEntity<List<TitleResponseDTO>> getAllTitles() {
        List<TitleResponseDTO> titles = titleService.getAllTitles();
        return ResponseEntity.ok(titles);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<DepartmentResponseDTO> depts = titleService.getAllDepartments();
        return ResponseEntity.ok(depts);
    }

    @GetMapping("/{deptName}")
    public ResponseEntity<List<TitleResponseDTO>> getAllByDept(@PathVariable String deptName) {
        List<TitleResponseDTO> titles = titleService.findByDept(deptName);
        return ResponseEntity.ok(titles);
    }

    @GetMapping("/{deptName}/{titleName}")
    public TitleResponseDTO getById(@PathVariable String deptName, @PathVariable String titleName) {
        TitleResponseDTO title = titleService.findByDepartmentAndTitle(deptName, titleName);
        return title;
    }

    /* Post Mappings */
    @PostMapping("/newTitle")
    public ResponseEntity<TitleResponseDTO> createTitle(@RequestBody TitleRequestDTO title) {
        TitleResponseDTO newTitle = titleService.createTitle(title);
        return ResponseEntity.ok(newTitle);
    }

    @PostMapping("/newDepartment")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@RequestBody DepartmentRequestDTO dept) {
        DepartmentResponseDTO newDept = titleService.createDepartment(dept);
        return ResponseEntity.ok(newDept);
    }

    /* Delete Mappings */
    @DeleteMapping("/{deptName}/{titleName}")
    public void deleteTitle(@PathVariable String deptName, @PathVariable String titleName) {
        titleService.deleteTitle(deptName, titleName);
    }
}
