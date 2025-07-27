package com.example.service;

import com.example.dto.DepartmentDTO;
import com.example.entity.Department;
import com.example.entity.Hospital;
import com.example.entity.Block;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.DepartmentRepository;
import com.example.repository.HospitalRepository;
import com.example.repository.BlockRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    private final BlockRepository blockRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                           HospitalRepository hospitalRepository,
                           BlockRepository blockRepository) {
        this.departmentRepository = departmentRepository;
        this.hospitalRepository = hospitalRepository;
        this.blockRepository = blockRepository;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Department createDepartment(DepartmentDTO departmentDTO) {
        log.info("Creating new department: {}", departmentDTO.getName());

        Hospital hospital = hospitalRepository.findById(departmentDTO.getHospitalId())
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + departmentDTO.getHospitalId()));

        Block block = null;
        if (departmentDTO.getBlockId() != null) {
            block = blockRepository.findById(departmentDTO.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + departmentDTO.getBlockId()));
        }

        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setHospital(hospital);
        department.setBlock(block);

        return departmentRepository.save(department);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Department getDepartment(Long departmentId) {
        log.info("Fetching department with ID: {}", departmentId);
        return departmentRepository.findById(departmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Department> getDepartmentsByHospital(Long hospitalId) {
        log.info("Fetching departments for hospital ID: {}", hospitalId);
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
        return departmentRepository.findByHospital(hospital);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Department> getDepartmentsByBlock(Long blockId) {
        log.info("Fetching departments for block ID: {}", blockId);
        return departmentRepository.findByBlockId(blockId);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Department updateDepartment(Long id, DepartmentDTO departmentDTO) {
        log.info("Updating department with ID: {}", id);

        Department department = getDepartment(id);

        if (departmentDTO.getName() != null) {
            department.setName(departmentDTO.getName());
        }
        if (departmentDTO.getDescription() != null) {
            department.setDescription(departmentDTO.getDescription());
        }
        if (departmentDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(departmentDTO.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + departmentDTO.getHospitalId()));
            department.setHospital(hospital);
        }
        if (departmentDTO.getBlockId() != null) {
            Block block = blockRepository.findById(departmentDTO.getBlockId())
                .orElseThrow(() -> new ResourceNotFoundException("Block not found with id: " + departmentDTO.getBlockId()));
            department.setBlock(block);
        }

        return departmentRepository.save(department);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void deleteDepartment(Long departmentId) {
        log.info("Deleting department with ID: {}", departmentId);
        Department department = getDepartment(departmentId);
        departmentRepository.delete(department);
    }
}
