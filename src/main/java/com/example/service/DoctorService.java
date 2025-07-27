package com.example.service;

import com.example.entity.Doctor;
import com.example.entity.Hospital;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.DoctorRepository;
import com.example.repository.HospitalRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;

    public DoctorService(DoctorRepository doctorRepository, HospitalRepository hospitalRepository) {
        this.doctorRepository = doctorRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Doctor createDoctor(Long hospitalId, Doctor doctor) {
        log.info("Creating new doctor for hospital ID: {}", hospitalId);
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));

        doctor.setHospital(hospital);
        return doctorRepository.save(doctor);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Doctor getDoctor(Long doctorId) {
        log.info("Fetching doctor with ID: {}", doctorId);
        return doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Doctor> getAllDoctors() {
        log.info("Fetching all doctors");
        return doctorRepository.findAll();
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Doctor> getDoctorsByHospital(Long hospitalId) {
        log.info("Fetching doctors for hospital ID: {}", hospitalId);
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
        return doctorRepository.findByHospital(hospital);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Doctor updateDoctor(Long doctorId, Doctor doctorDetails) {
        log.info("Updating doctor with ID: {}", doctorId);
        Doctor doctor = getDoctor(doctorId);
        doctor.setName(doctorDetails.getName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setEmail(doctorDetails.getEmail());
        return doctorRepository.save(doctor);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void deleteDoctor(Long doctorId) {
        log.info("Deleting doctor with ID: {}", doctorId);
        Doctor doctor = getDoctor(doctorId);
        doctorRepository.delete(doctor);
    }
}
