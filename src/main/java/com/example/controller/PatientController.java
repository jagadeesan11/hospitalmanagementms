package com.example.controller;

import com.example.dto.PatientDTO;
import com.example.entity.Patient;
import com.example.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patient Management", description = "APIs for managing hospital patients")
@Log4j2
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(summary = "Register a new patient")
    public ResponseEntity<Patient> createPatient(@RequestBody PatientDTO patientDTO) {
        log.info("Request received to register new patient");
        return ResponseEntity.ok(patientService.createPatient(patientDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        log.info("Request received to fetch patient with ID: {}", id);
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @GetMapping("/hospital/{hospitalId}")
    @Operation(summary = "Get all patients in a hospital")
    public ResponseEntity<List<Patient>> getHospitalPatients(@PathVariable Long hospitalId) {
        log.info("Request received to fetch all patients for hospital ID: {}", hospitalId);
        return ResponseEntity.ok(patientService.getHospitalPatients(hospitalId));
    }

    @GetMapping
    @Operation(summary = "Get all patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        log.info("Request received to fetch all patients");
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient information")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientDTO patientDTO) {
        log.info("Request received to update patient with ID: {}", id);
        return ResponseEntity.ok(patientService.updatePatient(id, patientDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        log.info("Request received to delete patient with ID: {}", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
