package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;
import com.sumerge.careertrack.user_management_svc.exceptions.TitleDoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.TitleDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

public class TitleService {

    @Autowired
    TitleRepository titleRepository;

    @Autowired
    TitleMapper titleMapper;

    public List<TitleDTO> getAll() {
        List<Title> titles = titleRepository.findAll();
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public List<TitleDTO> findByDept(String departmentName) {
        List<Title> titles = titleRepository.findByDepartment(departmentName);
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public TitleDTO createTitle(TitleDTO titleDTO) {
        Title title = titleMapper.toTitle(titleDTO);
        boolean titleExists = titleRepository
                .existsByDepartmentAndName(titleDTO.getDepartmentName(), titleDTO.getTitleName());
        if (titleExists) {
            throw new RuntimeException("Title already exists");
        }

        Title newTitle = titleRepository.save(title);
        return titleMapper.toDTO(newTitle);
    }

    public TitleDTO getById(String deptName, String titleName) {
        TitleId id = new TitleId(deptName, titleName);
        Title title = titleRepository.findById(id)
                .orElseThrow(() -> new TitleDoesNotExistException("Title not found"));
        return titleMapper.toDTO(title);
    }

    public void deleteTitle(String deptName, String titleName) {
        TitleId id = new TitleId(deptName, titleName);
        Title title = titleRepository.findById(id)
                .orElseThrow(
                        () -> new TitleDoesNotExistException("No Title with the ID %d found in the system!", id));
        titleRepository.delete(title);
    }
}
