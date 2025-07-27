package com.example.service;

import com.example.entity.Hospital;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.HospitalRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Log4j2
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Hospital createHospital(Hospital hospital) {
        log.info("Creating new hospital: {}", hospital.getName());
        return hospitalRepository.save(hospital);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Hospital getHospital(Long hospitalId) {
        log.info("Fetching hospital with ID: {}", hospitalId);
        return hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + hospitalId));
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Hospital> getAllHospitals() {
        log.info("Fetching all hospitals");
        return hospitalRepository.findAll();
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Hospital updateHospital(Long hospitalId, Hospital hospitalDetails) {
        log.info("Updating hospital with ID: {}", hospitalId);
        Hospital hospital = getHospital(hospitalId);
        hospital.setName(hospitalDetails.getName());
        hospital.setAddress(hospitalDetails.getAddress());
        hospital.setPhoneNumber(hospitalDetails.getPhoneNumber());
        hospital.setEmail(hospitalDetails.getEmail());
        return hospitalRepository.save(hospital);
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void deleteHospital(Long hospitalId) {
        log.info("Deleting hospital with ID: {}", hospitalId);
        Hospital hospital = getHospital(hospitalId);
        hospitalRepository.delete(hospital);
    }
}
