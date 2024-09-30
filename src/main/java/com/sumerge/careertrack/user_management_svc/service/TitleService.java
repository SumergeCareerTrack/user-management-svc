package com.sumerge.careertrack.user_management_svc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.TitleMapper;
import com.sumerge.careertrack.user_management_svc.mappers.TitleRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.TitleResponseDTO;
import com.sumerge.careertrack.user_management_svc.repositories.DepartmentRepository;
import com.sumerge.careertrack.user_management_svc.repositories.TitleRepository;

@Service
public class TitleService {

    @Autowired
    TitleRepository titleRepository;

    @Autowired
    TitleMapper titleMapper;

    @Autowired
    DepartmentRepository deptRepo;

    public List<TitleResponseDTO> getAll() {
        List<Title> titles = titleRepository.findAll();
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public List<TitleResponseDTO> findByDept(String departmentName) {
        List<Title> titles = titleRepository.findByDepartmentName(departmentName);
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }

    public TitleResponseDTO createTitle(TitleRequestDTO dto) {
        Title title = titleMapper.toTitle(dto);
        Department dept = deptRepo.findById(dto.getDepartmentId())
                .orElseThrow(() -> new DoesNotExistException(DoesNotExistException.DEPARTMENT, dto.getDepartmentId()));

        boolean titleExists = titleRepository
                .existsByNameAndDepartmentName(dto.getName(), dto.getName());

        if (titleExists) {
            throw new AlreadyExistsException(AlreadyExistsException.TITLE,
                    dto.getName(), dept.getName());
        }

        Title newTitle = titleRepository.save(title);
        return titleMapper.toDTO(newTitle);
    }

    public TitleResponseDTO findByDepartmentAndTitle(String deptName, String titleName) {
        Title title = titleRepository.findByNameAndDepartmentName(titleName, deptName)
                .orElseThrow(() -> new DoesNotExistException(DoesNotExistException.TITLE,
                        titleName, deptName));

        return titleMapper.toDTO(title);
    }

    public void deleteTitle(String deptName, String titleName) {
        Title title = titleRepository.findByNameAndDepartmentName(titleName, deptName)
                .orElseThrow(
                        () -> new DoesNotExistException(DoesNotExistException.TITLE,
                                titleName, deptName));

        titleRepository.delete(title);
    }
}
