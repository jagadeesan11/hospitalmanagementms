package com.example.controller;

import com.example.entity.Hospital;
import com.example.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@Tag(name = "Hospital Management", description = "APIs for managing hospitals")
@Log4j2
public class HospitalController {
    private final HospitalService hospitalService;

    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @PostMapping
    @Operation(summary = "Create a new hospital")
    public ResponseEntity<Hospital> createHospital(@RequestBody Hospital hospital) {
        log.info("Request received to create new hospital");
        return ResponseEntity.ok(hospitalService.createHospital(hospital));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hospital by ID")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        log.info("Request received to fetch hospital with ID: {}", id);
        return ResponseEntity.ok(hospitalService.getHospital(id));
    }

    @GetMapping
    @Operation(summary = "Get all hospitals")
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        log.info("Request received to fetch all hospitals");
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hospital information")
    public ResponseEntity<Hospital> updateHospital(
            @PathVariable Long id,
            @RequestBody Hospital hospital) {
        log.info("Request received to update hospital with ID: {}", id);
        return ResponseEntity.ok(hospitalService.updateHospital(id, hospital));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hospital")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        log.info("Request received to delete hospital with ID: {}", id);
        hospitalService.deleteHospital(id);
        return ResponseEntity.noContent().build();
    }
}
