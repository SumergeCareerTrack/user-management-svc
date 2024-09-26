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

import com.sumerge.careertrack.user_management_svc.service.TitleService;

@RestController
@RequestMapping("/titles")
public class TitleController {
    
    @Autowired
    private TitleService titleService;

    public TitleController(TitleService titleService) {
        this.titleService = titleService;
    }

    /* Get Mappings */
    @GetMapping("/")
    public ResponseEntity<List<TitleDTO>> getAllTitles() {
        List<TitleDTO> titles = titleService.findAlltitle();
        return ResponseEntity.ok(titles);
    }
    @GetMapping("/department")
    public ResponseEntity<List<TitleDTO>> getTitlesByDep(UUID depId) {
        List<TitleDTO> titles = titleService.findtitleByDep(depId);
        return ResponseEntity.ok(titles);
    }
    @GetMapping("/manager")
    public ResponseEntity<List<TitleDTO>> getManagerTitles() {
        List<TitleDTO> titles = titleService.findManagertitle();
        return ResponseEntity.ok(titles);
    }

    /* Post Mappings */
    @PostMapping("/")
    public ResponseEntity<TitleDTO> addTitle(TitleDTO title) {
        TitleDTO newTitle = titleService.addTitle(title);
        return ResponseEntity.ok(newTitle);
    }

    /* Put Mappings */
    @PutMapping("/")
    public ResponseEntity<TitleDTO> transferTitleDep(TitleDTO title, UUID depId) {
        TitleDTO newTitle = titleService.transferTitleDep(title, depId);
        return ResponseEntity.ok(newTitle);
    }

    /* Delete Mappings */
    @DeleteMapping("/")
    public void deleteTitle(UUID titleId) {
        titleService.deleteTitle(titleId);
    }
    

}
