package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

public class TitleService {
    
    @Autowired
    TitleRepository titleRepository;

    @Autowired
    TitleMapper titleMapper;

    public List<TitleDTO> findAlltitle() {
        List<Title> titles = titleRepository.findAll()
        .orElseThrow(() -> new titleDoesNotExistException("No titles found in the system!"));
        return titles.stream().map(titleMapper::mapToTitleDTO).collect(Collectors.toList());
    }

    public List<TitleDTO> findtitleByDep(String departmentName) {
        List<Title> titles = titleRepository.findByDepName(departmentName)
        .orElseThrow(() -> new titleDoesNotExistException("No titles in the department : [ %d ] found in the system!",departmentName));
        return titles.stream().map(titleMapper::mapToTitleDTO).collect(Collectors.toList());
    }

    public List<TitleDTO> findManagertitle() {
        List<Title> titles = titleRepository.findManagerTrue()
        .orElseThrow(() -> new titleDoesNotExistException("No manager title found"));
        return titles.stream().map(titleMapper::mapToTitleDTO).collect(Collectors.toList());
    }

    public TitleDTO addTitle(TitleDTO TitleDTO) {
        Title title = titleMapper.mapTotitle(TitleDTO);
        boolean titleExists = titleRepository.findByTitle(title.getTitle()).orElse(false);
        if(titleExists) {
            throw new RuntimeException("Title already exists");
        }
        
        Title newTitle = titleRepository.save(title);
        return titleMapper.mapToTitleDTO(newTitle);
    }

    public void deleteTitle(UUID titleId){
        Title title = titleRepository.findById(titleId)
        .orElseThrow(() -> new titleDoesNotExistException("No Title with the ID %d found in the system!",titleId));
        titleRepository.delete(title);
    }

}
