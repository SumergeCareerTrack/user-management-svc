package com.sumerge.careertrack.user_management_svc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.service.TitleService;

@RestController
@RequestMapping("/titles")
public class TitleController {

    @Autowired
    private TitleService titleService;

    /* Get Mappings */
    @GetMapping("/")
    public ResponseEntity<List<TitleResponseDTO>> getAll() {
        List<TitleResponseDTO> titles = titleService.getAll();
        return ResponseEntity.ok(titles);
    }

    @GetMapping("/{deptName}")
    public ResponseEntity<List<TitleResponseDTO>> getAllByDept(String deptName) {
        List<TitleResponseDTO> titles = titleService.findByDept(deptName);
        return ResponseEntity.ok(titles);
    }

    @GetMapping("/{deptName}/{titleName}")
    public TitleResponseDTO getById(@PathVariable String deptName, @PathVariable String titleName) {
        TitleResponseDTO title = titleService.findByDepartmentAndTitle(deptName, titleName);
        return title;
    }

    /* Post Mappings */
    @PostMapping("/")
    public ResponseEntity<TitleResponseDTO> createTitle(@RequestBody TitleRequestDTO title) {
        System.out.println(title);
        TitleResponseDTO newTitle = titleService.createTitle(title);
        return ResponseEntity.ok(newTitle);
    }

    /* Delete Mappings */
    @DeleteMapping("/{deptName}/{titleName}")
    public void deleteTitle(@PathVariable String deptName, @PathVariable String titleName) {
        titleService.deleteTitle(deptName, titleName);
    }
}
