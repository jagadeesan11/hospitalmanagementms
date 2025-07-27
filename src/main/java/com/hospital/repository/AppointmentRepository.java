package com.hospital.repository;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorAndAppointmentTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatientEmail(String email);
}
