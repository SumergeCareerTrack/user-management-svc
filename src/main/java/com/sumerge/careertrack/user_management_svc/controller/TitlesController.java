package com.sumerge.careertrack.user_management_svc.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/titles")
public class TitlesController {
    
    @Autowired
    private TitlesService titlesService;

    public TitlesController(TitlesService titlesService) {
        this.titlesService = titlesService;
    }

    /* Get Mappings */
    @GetMapping("/")
    public ResponseEntity<List<TitlesDTO>> getAllTitles() {
        List<TitlesDTO> titles = titlesService.findAllTitles();
        return ResponseEntity.ok(titles);
    }
    @GetMapping("/department")
    public ResponseEntity<List<TitlesDTO>> getTitlesByDep(UUID depId) {
        List<TitlesDTO> titles = titlesService.findTitlesByDep(depId);
        return ResponseEntity.ok(titles);
    }
    @GetMapping("/manager")
    public ResponseEntity<List<TitlesDTO>> getManagerTitles() {
        List<TitlesDTO> titles = titlesService.findManagerTitles();
        return ResponseEntity.ok(titles);
    }

    /* Post Mappings */
    @PostMapping("/")
    public ResponseEntity<TitlesDTO> addTitle(TitlesDTO title) {
        TitlesDTO newTitle = titlesService.addTitle(title);
        return ResponseEntity.ok(newTitle);
    }

    /* Put Mappings */
    @PutMapping("/")
    public ResponseEntity<TitlesDTO> transferTitleDep(TitlesDTO title, UUID depId) {
        TitlesDTO newTitle = titlesService.transferTitleDep(title, depId);
        return ResponseEntity.ok(newTitle);
    }

    /* Delete Mappings */
    @DeleteMapping("/")
    public void deleteTitle(UUID titleId) {
        titlesService.deleteTitle(titleId);
    }
    

}
