package com.sumerge.careertrack.user_management_svc.controllers;

import java.util.List;

import com.sumerge.careertrack.user_management_svc.mappers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumerge.careertrack.user_management_svc.services.TitleService;

@CrossOrigin
@RestController
@RequestMapping("/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /* Get Mappings */
    @GetMapping("/")
    public ResponseEntity<List<TitleResponseDTO>> getAllTitles(@RequestParam(required = false) Integer page,
                                                               @RequestParam(required = false) Integer size) {
        if (page == null || size == null || size == 0) {
            List<TitleResponseDTO> allTitles = titleService.getAllTitles();
            return ResponseEntity.ok(allTitles);
        } else {
            Pageable pageable = PageRequest.of(page, size);
            Page<TitleResponseDTO> titlesPage = titleService.getAllTitles(pageable);
            List<TitleResponseDTO> titles = titlesPage.getContent();

            return ResponseEntity.ok(titles);
        }
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
