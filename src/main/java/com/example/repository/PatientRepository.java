package com.example.repository;

import com.example.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByHospitalId(Long hospitalId);
    Page<Patient> findByHospitalId(Long hospitalId, Pageable pageable);
    boolean existsByEmailAndHospitalId(String email, Long hospitalId);
    Optional<Patient> findByEmailAndHospitalId(String email, Long hospitalId);
    Optional<Patient> findByPhone(String phone);
}
