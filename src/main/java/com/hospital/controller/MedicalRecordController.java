package com.hospital.controller;

import com.hospital.dto.MedicalRecordDTO;
import com.hospital.entity.MedicalRecord;
import com.hospital.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records Management", description = "APIs for managing patient medical records")
@Log4j2
public class MedicalRecordController {
    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @Operation(summary = "Create a new medical record")
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecordDTO recordDTO) {
        log.info("Request received to create medical record for patient ID: {}", recordDTO.getPatientId());
        return ResponseEntity.ok(medicalRecordService.createMedicalRecord(recordDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medical record by ID")
    public ResponseEntity<MedicalRecord> getMedicalRecord(@PathVariable Long id) {
        log.info("Request received to fetch medical record with ID: {}", id);
        return ResponseEntity.ok(medicalRecordService.getMedicalRecord(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient's complete medical history")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalHistory(@PathVariable Long patientId) {
        log.info("Request received to fetch medical history for patient ID: {}", patientId);
        return ResponseEntity.ok(medicalRecordService.getPatientMedicalHistory(patientId));
    }

    @GetMapping("/patient/{patientId}/paged")
    @Operation(summary = "Get patient's medical history with pagination")
    public ResponseEntity<Page<MedicalRecord>> getPatientMedicalHistoryPaged(
            @PathVariable Long patientId,
            Pageable pageable) {
        log.info("Request received to fetch paged medical history for patient ID: {}", patientId);
        return ResponseEntity.ok(medicalRecordService.getPatientMedicalHistoryPaged(patientId, pageable));
    }

    @GetMapping("/patient/{patientId}/date-range")
    @Operation(summary = "Get patient's medical history within a date range")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalHistoryByDateRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("Request received to fetch medical history for patient ID: {} between {} and {}",
                patientId, start, end);
        return ResponseEntity.ok(medicalRecordService.getPatientMedicalHistoryByDateRange(patientId, start, end));
    }

    @GetMapping("/patient/{patientId}/type/{recordType}")
    @Operation(summary = "Get patient's medical history by record type")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalHistoryByType(
            @PathVariable Long patientId,
            @PathVariable MedicalRecord.RecordType recordType) {
        log.info("Request received to fetch medical history for patient ID: {} of type: {}",
                patientId, recordType);
        return ResponseEntity.ok(medicalRecordService.getPatientMedicalHistoryByType(patientId, recordType));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing medical record")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecordDTO recordDTO) {
        log.info("Request received to update medical record with ID: {}", id);
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(id, recordDTO));
    }
}
