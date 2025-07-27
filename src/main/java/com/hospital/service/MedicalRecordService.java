package com.hospital.service;

import com.hospital.dto.MedicalRecordDTO;
import com.hospital.entity.Doctor;
import com.hospital.entity.MedicalRecord;
import com.hospital.entity.Patient;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.MedicalRecordRepository;
import com.hospital.repository.PatientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecordDTO recordDTO) {
        log.info("Creating medical record for patient ID: {}", recordDTO.getPatientId());

        Patient patient = patientRepository.findById(recordDTO.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + recordDTO.getPatientId()));

        Doctor doctor = doctorRepository.findById(recordDTO.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + recordDTO.getDoctorId()));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setTreatingDoctor(doctor);
        record.setDiagnosis(recordDTO.getDiagnosis());
        record.setSymptoms(recordDTO.getSymptoms());
        record.setTreatment(recordDTO.getTreatment());
        record.setPrescriptions(recordDTO.getPrescriptions());
        record.setNotes(recordDTO.getNotes());
        record.setAllergies(recordDTO.getAllergies());
        record.setVisitDate(recordDTO.getVisitDate() != null ? recordDTO.getVisitDate() : LocalDateTime.now());
        record.setRecordType(recordDTO.getRecordType());

        // Convert and set vital signs
        if (recordDTO.getVitalSigns() != null) {
            List<MedicalRecord.VitalSign> vitalSigns = recordDTO.getVitalSigns().stream()
                .map(vitalSignDTO -> {
                    MedicalRecord.VitalSign vitalSign = new MedicalRecord.VitalSign();
                    vitalSign.setType(vitalSignDTO.getType());
                    vitalSign.setValue(vitalSignDTO.getValue());
                    vitalSign.setUnit(vitalSignDTO.getUnit());
                    vitalSign.setRecordedAt(vitalSignDTO.getRecordedAt() != null ?
                        vitalSignDTO.getRecordedAt() : LocalDateTime.now());
                    return vitalSign;
                })
                .collect(Collectors.toList());
            record.setVitalSigns(vitalSigns);
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        log.info("Medical record created successfully with ID: {}", saved.getId());
        return saved;
    }

    public MedicalRecord getMedicalRecord(Long recordId) {
        log.info("Fetching medical record with ID: {}", recordId);
        return medicalRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + recordId));
    }

    public List<MedicalRecord> getPatientMedicalHistory(Long patientId) {
        log.info("Fetching medical history for patient ID: {}", patientId);
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    public Page<MedicalRecord> getPatientMedicalHistoryPaged(Long patientId, Pageable pageable) {
        log.info("Fetching paged medical history for patient ID: {}", patientId);
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId, pageable);
    }

    public List<MedicalRecord> getPatientMedicalHistoryByDateRange(Long patientId, LocalDateTime start, LocalDateTime end) {
        log.info("Fetching medical history for patient ID: {} between {} and {}", patientId, start, end);
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return medicalRecordRepository.findByPatientIdAndVisitDateBetween(patientId, start, end);
    }

    public List<MedicalRecord> getPatientMedicalHistoryByType(Long patientId, MedicalRecord.RecordType recordType) {
        log.info("Fetching medical history for patient ID: {} of type: {}", patientId, recordType);
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        return medicalRecordRepository.findByPatientIdAndRecordType(patientId, recordType);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(Long recordId, MedicalRecordDTO recordDTO) {
        log.info("Updating medical record with ID: {}", recordId);

        MedicalRecord existingRecord = getMedicalRecord(recordId);

        if (recordDTO.getDiagnosis() != null) {
            existingRecord.setDiagnosis(recordDTO.getDiagnosis());
        }
        if (recordDTO.getSymptoms() != null) {
            existingRecord.setSymptoms(recordDTO.getSymptoms());
        }
        if (recordDTO.getTreatment() != null) {
            existingRecord.setTreatment(recordDTO.getTreatment());
        }
        if (recordDTO.getPrescriptions() != null) {
            existingRecord.setPrescriptions(recordDTO.getPrescriptions());
        }
        if (recordDTO.getNotes() != null) {
            existingRecord.setNotes(recordDTO.getNotes());
        }
        if (recordDTO.getAllergies() != null) {
            existingRecord.setAllergies(recordDTO.getAllergies());
        }
        if (recordDTO.getVitalSigns() != null) {
            List<MedicalRecord.VitalSign> vitalSigns = recordDTO.getVitalSigns().stream()
                .map(vitalSignDTO -> {
                    MedicalRecord.VitalSign vitalSign = new MedicalRecord.VitalSign();
                    vitalSign.setType(vitalSignDTO.getType());
                    vitalSign.setValue(vitalSignDTO.getValue());
                    vitalSign.setUnit(vitalSignDTO.getUnit());
                    vitalSign.setRecordedAt(vitalSignDTO.getRecordedAt());
                    return vitalSign;
                })
                .collect(Collectors.toList());
            existingRecord.setVitalSigns(vitalSigns);
        }
        if (recordDTO.getRecordType() != null) {
            existingRecord.setRecordType(recordDTO.getRecordType());
        }
        if (recordDTO.getVisitDate() != null) {
            existingRecord.setVisitDate(recordDTO.getVisitDate());
        }

        return medicalRecordRepository.save(existingRecord);
    }
}
