package com.example.service;

import com.example.dto.PatientDTO;
import com.example.entity.Hospital;
import com.example.entity.Patient;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.HospitalRepository;
import com.example.repository.PatientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class PatientService {
    private final PatientRepository patientRepository;
    private final HospitalRepository hospitalRepository;

    public PatientService(PatientRepository patientRepository, HospitalRepository hospitalRepository) {
        this.patientRepository = patientRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Transactional
    public Patient createPatient(PatientDTO patientDTO) {
        log.info("Creating new patient with email: {}", patientDTO.getEmail());

        if (patientRepository.existsByEmailAndHospitalId(patientDTO.getEmail(), patientDTO.getHospitalId())) {
            throw new IllegalStateException("Patient already exists in this hospital with email: " + patientDTO.getEmail());
        }

        Hospital hospital = hospitalRepository.findById(patientDTO.getHospitalId())
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + patientDTO.getHospitalId()));

        Patient patient = new Patient();
        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setAddress(patientDTO.getAddress());
        patient.setGender(patientDTO.getGender());
        patient.setBloodGroup(patientDTO.getBloodGroup());
        patient.setHospital(hospital);

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        return savedPatient;
    }

    public Patient getPatient(Long patientId) {
        log.info("Fetching patient with ID: {}", patientId);
        return patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
    }

    public List<Patient> getHospitalPatients(Long hospitalId) {
        log.info("Fetching all patients for hospital ID: {}", hospitalId);
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException("Hospital not found with id: " + hospitalId);
        }
        return patientRepository.findByHospitalId(hospitalId);
    }

    public List<Patient> getAllPatients() {
        log.info("Fetching all patients");
        return patientRepository.findAll();
    }

    @Transactional
    public Patient updatePatient(Long patientId, PatientDTO patientDTO) {
        log.info("Updating patient with ID: {}", patientId);

        Patient patient = getPatient(patientId);

        // Check if email is being changed and if new email already exists
        if (!patient.getEmail().equals(patientDTO.getEmail()) &&
            patientRepository.existsByEmailAndHospitalId(patientDTO.getEmail(), patientDTO.getHospitalId())) {
            throw new IllegalStateException("Email already in use: " + patientDTO.getEmail());
        }

        patient.setFirstName(patientDTO.getFirstName());
        patient.setLastName(patientDTO.getLastName());
        patient.setEmail(patientDTO.getEmail());
        patient.setPhone(patientDTO.getPhone());
        patient.setDateOfBirth(patientDTO.getDateOfBirth());
        patient.setAddress(patientDTO.getAddress());
        patient.setGender(patientDTO.getGender());
        patient.setBloodGroup(patientDTO.getBloodGroup());

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully");
        return updatedPatient;
    }

    @Transactional
    public void deletePatient(Long patientId) {
        log.info("Deleting patient with ID: {}", patientId);
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        patientRepository.deleteById(patientId);
        log.info("Patient deleted successfully");
    }
}
