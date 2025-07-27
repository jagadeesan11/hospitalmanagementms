package com.hospital.controller;

import com.hospital.dto.DepartmentDTO;
import com.hospital.entity.Department;
import com.hospital.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Department Management", description = "APIs for managing hospital departments")
@Log4j2
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/hospitals/{hospitalId}/blocks/{blockId}/departments")
    @Operation(summary = "Create a new department in a hospital block")
    public ResponseEntity<Department> createDepartment(
            @PathVariable Long hospitalId,
            @PathVariable Long blockId,
            @RequestBody DepartmentDTO departmentDTO) {
        log.info("Request received to create department in hospital ID: {} and block ID: {}", hospitalId, blockId);

        // Set path variables into DTO
        departmentDTO.setHospitalId(hospitalId);
        departmentDTO.setBlockId(blockId);

        return ResponseEntity.ok(departmentService.createDepartment(departmentDTO));
    }

    @PostMapping("/departments")
    @Operation(summary = "Create a new department")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("Request received to create department: {}", departmentDTO.getName());
        return ResponseEntity.ok(departmentService.createDepartment(departmentDTO));
    }

    @GetMapping("/departments/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<Department> getDepartment(@PathVariable Long id) {
        log.info("Request received to fetch department with ID: {}", id);
        return ResponseEntity.ok(departmentService.getDepartment(id));
    }

    @GetMapping("/hospitals/{hospitalId}/departments")
    @Operation(summary = "Get all departments in a hospital")
    public ResponseEntity<List<Department>> getDepartmentsByHospital(@PathVariable Long hospitalId) {
        log.info("Request received to fetch departments for hospital ID: {}", hospitalId);
        return ResponseEntity.ok(departmentService.getDepartmentsByHospital(hospitalId));
    }

    @GetMapping("/blocks/{blockId}/departments")
    @Operation(summary = "Get all departments in a block")
    public ResponseEntity<List<Department>> getDepartmentsByBlock(@PathVariable Long blockId) {
        log.info("Request received to fetch departments for block ID: {}", blockId);
        return ResponseEntity.ok(departmentService.getDepartmentsByBlock(blockId));
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentDTO departmentDTO) {
        log.info("Request received to update department with ID: {}", id);
        return ResponseEntity.ok(departmentService.updateDepartment(id, departmentDTO));
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        log.info("Request received to delete department with ID: {}", id);
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
