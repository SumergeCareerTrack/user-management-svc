package com.sumerge.careertrack.user_management_svc.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sumerge.careertrack.user_management_svc.entities.Department;
import com.sumerge.careertrack.user_management_svc.entities.Title;
import com.sumerge.careertrack.user_management_svc.exceptions.AlreadyExistsException;
import com.sumerge.careertrack.user_management_svc.exceptions.DoesNotExistException;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentMapper;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentRequestDTO;
import com.sumerge.careertrack.user_management_svc.mappers.DepartmentResponseDTO;
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

    @Autowired
    DepartmentMapper deptMapper;

    public List<TitleResponseDTO> getAllTitles() {
        List<Title> titles = titleRepository.findAll();
        return titles.stream().map(titleMapper::toDTO).collect(Collectors.toList());
    }
    public Page<TitleResponseDTO> getAllTitles(Pageable pageable) {
        Page<Title> titles = titleRepository.findAll(pageable);
        return titles.map(titleMapper::toDTO);
    }
    public TitleResponseDTO getTitleById(String id){
        Title title = titleRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new DoesNotExistException(DoesNotExistException.TITLE, id));
        return titleMapper.toDTO(title);
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        List<Department> titles = deptRepo.findAll();
        return titles.stream().map(deptMapper::toDTO).collect(Collectors.toList());
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
                .existsByNameAndDepartmentName(dto.getName(), dept.getName());

        if (titleExists) {
            throw new AlreadyExistsException(AlreadyExistsException.TITLE,
                    dto.getName(), dept.getName());
        }

        title.setDepartment(dept);
        Title newTitle = titleRepository.save(title);
        return titleMapper.toDTO(newTitle);
    }

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {
        boolean deptExists = deptRepo.existsByName(dto.getName());

        if (deptExists) {
            throw new AlreadyExistsException(AlreadyExistsException.DEPARTMENT, dto.getName());
        }

        Department dept = Department.builder().name(dto.getName()).build();
        deptRepo.save(dept);
        return deptMapper.toDTO(dept);
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
