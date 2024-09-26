package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

public class TitlesService {
    
    @Autowired
    TitlesRepository titlesRepository;

    @Autowired
    TitlesMapper titlesMapper;

    public List<TitlesDTO> findAllTitles() {
        List<Titles> users = titlesRepository.findAll()
        .orElseThrow(() -> new TitlesDoesNotExistException("No Titles found in the system!"));
        return users.stream().map(titlesMapper::mapToTitlesDTO).collect(Collectors.toList());
    }

    public List<TitlesDTO> findTitlesByDep(UUID depId) {
        List<Titles> users = titlesRepository.findByDepId(depId)
        .orElseThrow(() -> new TitlesDoesNotExistException("No Titles in the department with ID %d found in the system!",depId));
        return users.stream().map(titlesMapper::mapToTitlesDTO).collect(Collectors.toList());
    }

    public List<TitlesDTO> findManagerTitles() {
        List<Titles> users = titlesRepository.findManagerTitles()
        .orElseThrow(() -> new TitlesDoesNotExistException("No manager titles found"));
        return users.stream().map(titlesMapper::mapToTitlesDTO).collect(Collectors.toList());
    }

    public TitlesDTO addTitle(TitlesDTO titleDTO) {
        Titles title = titlesMapper.mapToTitles(titleDTO);
        boolean titleExists = titlesRepository.findByTitle(title.getTitle()).orElse(false);
        if(titleExists) {
            throw new RuntimeException("Title already exists");
        }
        
        Titles newTitle = titlesRepository.save(title);
        return titlesMapper.mapToTitlesDTO(newTitle);
    }

    public void deleteTitle(UUID titleId){
        Titles title = titlesRepository.findById(titleId)
        .orElseThrow(() -> new TitlesDoesNotExistException("No Title with the ID %d found in the system!",titleId));
        titlesRepository.delete(title);
    }

}
