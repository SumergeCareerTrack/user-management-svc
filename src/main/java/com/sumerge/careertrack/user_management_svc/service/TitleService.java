package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.entities.compositeKeys.TitleId;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@Service
public class TitleService {

    @Autowired
    TitleRepository titleRepository;

    @Autowired
    TitleMapper titleMapper;

    public List<TitleResponseDTO> getAll() {
        List<Title> titles = titleRepository.findAll();
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public List<TitleResponseDTO> findByDept(String departmentName) {
        List<Title> titles = titleRepository.findByIdDepartment(departmentName);
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public TitleResponseDTO createTitle(TitleRequestDTO titleDTO) {
        Title title = titleMapper.toTitle(titleDTO);
        boolean titleExists = titleRepository
                .existsById(new TitleId(titleDTO.getDepartmentName(), titleDTO.getTitleName()));
        if (titleExists) {
            throw new AlreadyExistsException(AlreadyExistsException.TITLE,
                    titleDTO.getTitleName(), titleDTO.getDepartmentName());
        }

        Title newTitle = titleRepository.save(title);
        return titleMapper.toDTO(newTitle);
    }

    public TitleResponseDTO getById(String deptName, String titleName) {
        TitleId id = new TitleId(deptName, titleName);
        Title title = titleRepository.findById(id)
                .orElseThrow(() -> new DoesNotExistException(DoesNotExistException.TITLE,
                        titleName, deptName));
        return titleMapper.toDTO(title);
    }

    public void deleteTitle(String deptName, String titleName) {
        TitleId id = new TitleId(deptName, titleName);
        Title title = titleRepository.findById(id)
                .orElseThrow(
                        () -> new DoesNotExistException(DoesNotExistException.TITLE,
                                titleName, deptName));
        titleRepository.delete(title);
    }
}
