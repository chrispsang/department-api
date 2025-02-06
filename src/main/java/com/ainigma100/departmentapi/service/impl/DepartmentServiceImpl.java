package com.ainigma100.departmentapi.service.impl;

import com.ainigma100.departmentapi.dto.DepartmentDTO;
import com.ainigma100.departmentapi.dto.DepartmentSearchCriteriaDTO;
import com.ainigma100.departmentapi.entity.Department;
import com.ainigma100.departmentapi.exception.ResourceAlreadyExistException;
import com.ainigma100.departmentapi.exception.ResourceNotFoundException;
import com.ainigma100.departmentapi.mapper.DepartmentMapper;
import com.ainigma100.departmentapi.repository.DepartmentRepository;
import com.ainigma100.departmentapi.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department data must not be null");
        }
        if (departmentDTO.getDepartmentCode() == null || departmentDTO.getDepartmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Department code must not be null or empty");
        }
        if (!departmentDTO.getDepartmentCode().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Department code must be alphanumeric");
        }
        if (departmentDTO.getDepartmentName() == null || departmentDTO.getDepartmentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name must not be empty");
        }

        if (departmentRepository.findByDepartmentCode(departmentDTO.getDepartmentCode()) != null) {
            throw new ResourceAlreadyExistException("Resource Department with departmentCode : '"
                    + departmentDTO.getDepartmentCode() + "' already exists");
        }

        Department department = departmentMapper.departmentDtoToDepartment(departmentDTO);
        return departmentMapper.departmentToDepartmentDto(departmentRepository.save(department));
    }

    @Override
    public Page<DepartmentDTO> getAllDepartmentsUsingPagination(DepartmentSearchCriteriaDTO departmentSearchCriteriaDTO) {
        List<Department> departments = departmentRepository.getAllDepartmentsUsingPagination(departmentSearchCriteriaDTO, Pageable.unpaged()).getContent();
        return new PageImpl<>(departmentMapper.departmentToDepartmentDto(departments));
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Department ID must not be null");
        }

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id : '" + id + "' not found"));
        
        return departmentMapper.departmentToDepartmentDto(department);
    }

    @Override
    public DepartmentDTO updateDepartment(DepartmentDTO departmentDTO, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Department ID must not be null");
        }
        if (departmentDTO == null) {
            throw new IllegalArgumentException("Department data must not be null");
        }
        if (departmentDTO.getDepartmentCode() == null || departmentDTO.getDepartmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Department code must not be null or empty");
        }
        if (!departmentDTO.getDepartmentCode().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Department code must be alphanumeric");
        }
        if (departmentDTO.getDepartmentName() == null || departmentDTO.getDepartmentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name must not be empty");
        }

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id : '" + id + "' not found"));

        existingDepartment.setDepartmentCode(departmentDTO.getDepartmentCode());
        existingDepartment.setDepartmentName(departmentDTO.getDepartmentName());
        existingDepartment.setDepartmentDescription(departmentDTO.getDepartmentDescription());

        return departmentMapper.departmentToDepartmentDto(departmentRepository.save(existingDepartment));
    }

    @Override
    public void deleteDepartment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Department ID must not be null");
        }

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department with id : '" + id + "' not found"));

        departmentRepository.delete(department);
    }

    
}
