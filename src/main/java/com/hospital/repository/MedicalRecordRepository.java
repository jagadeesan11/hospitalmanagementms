package com.hospital.repository;

import com.hospital.entity.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);
    Page<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId, Pageable pageable);
    List<MedicalRecord> findByPatientIdAndVisitDateBetween(Long patientId, LocalDateTime start, LocalDateTime end);
    List<MedicalRecord> findByPatientIdAndRecordType(Long patientId, MedicalRecord.RecordType recordType);
    List<MedicalRecord> findByTreatingDoctorId(Long doctorId);
}
