package com.hospital.controller;

import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Doctor Management", description = "APIs for managing doctors")
@Log4j2
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/hospitals/{hospitalId}/doctors")
    @Operation(summary = "Create a new doctor in a hospital")
    public ResponseEntity<Doctor> createDoctor(
            @PathVariable Long hospitalId,
            @RequestBody Doctor doctor) {
        log.info("Request received to create doctor in hospital ID: {}", hospitalId);
        return ResponseEntity.ok(doctorService.createDoctor(hospitalId, doctor));
    }

    @GetMapping("/doctors/{id}")
    @Operation(summary = "Get doctor by ID")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        log.info("Request received to fetch doctor with ID: {}", id);
        return ResponseEntity.ok(doctorService.getDoctor(id));
    }

    @GetMapping("/doctors")
    @Operation(summary = "Get all doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        log.info("Request received to fetch all doctors");
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/hospitals/{hospitalId}/doctors")
    @Operation(summary = "Get all doctors in a hospital")
    public ResponseEntity<List<Doctor>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        log.info("Request received to fetch doctors for hospital ID: {}", hospitalId);
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }

    @PutMapping("/doctors/{id}")
    @Operation(summary = "Update doctor information")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor) {
        log.info("Request received to update doctor with ID: {}", id);
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    @DeleteMapping("/doctors/{id}")
    @Operation(summary = "Delete doctor")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        log.info("Request received to delete doctor with ID: {}", id);
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
