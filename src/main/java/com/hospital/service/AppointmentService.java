package com.hospital.service;

import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.exception.AppointmentConflictException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                            DoctorRepository doctorRepository,
                            PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Appointment createAppointment(AppointmentDTO appointmentDTO) {
        log.info("Creating appointment for doctor ID: {} and patient ID: {}",
                appointmentDTO.getDoctor().getId(), appointmentDTO.getPatient().getId());

        if (appointmentDTO.getAppointmentTime() == null) {
            log.error("Appointment time cannot be null");
            throw new IllegalArgumentException("Appointment time is required");
        }

        if (appointmentDTO.getAppointmentTime().isBefore(LocalDateTime.now())) {
            log.error("Cannot create appointment in the past");
            throw new IllegalArgumentException("Appointment time must be in the future");
        }

        // Fetch doctor and patient entities
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctor().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + appointmentDTO.getDoctor().getId()));

        Patient patient = null;
        if(appointmentDTO.getPatient().getId() != null) {
             patient = patientRepository.findById(appointmentDTO.getPatient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + appointmentDTO.getPatient().getId()));
        } else {
            // Try to find patient by phone, create new if not found
            Optional<Patient> existingPatient = patientRepository.findByPhone(appointmentDTO.getPatient().getPhone());

            if (existingPatient.isPresent()) {
                patient = existingPatient.get();
                log.info("Found existing patient by phone: {}", appointmentDTO.getPatient().getPhone());
            } else {
                // Create new patient if not found by phone
                log.info("Patient not found by phone: {}, creating new patient", appointmentDTO.getPatient().getPhone());
                patient = new Patient();
                patient.setFirstName(appointmentDTO.getPatient().getFirstName());
                patient.setLastName(appointmentDTO.getPatient().getLastName());
                patient.setEmail(appointmentDTO.getPatient().getEmail());
                patient.setPhone(appointmentDTO.getPatient().getPhone());
                patient.setDateOfBirth(appointmentDTO.getPatient().getDateOfBirth());
                patient.setAddress(appointmentDTO.getPatient().getAddress());
                patient.setGender(appointmentDTO.getPatient().getGender());
                patient.setBloodGroup(appointmentDTO.getPatient().getBloodGroup());
                patient.setHospital(doctor.getHospital()); // Assign to same hospital as doctor

                patient = patientRepository.save(patient);
                log.info("New patient created successfully with ID: {}", patient.getId());
            }
        }

        // Verify patient belongs to same hospital as doctor
        if (!patient.getHospital().getId().equals(doctor.getHospital().getId())) {
            throw new IllegalStateException("Patient and Doctor must belong to the same hospital");
        }

        // Check for conflicts
        checkAppointmentConflicts(doctor.getId(), appointmentDTO.getAppointmentTime());

        // Create new appointment entity
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(appointmentDTO.getAppointmentTime());
        appointment.setStatus(appointmentDTO.getStatus() != null ?
            appointmentDTO.getStatus() : Appointment.AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getId());
        return savedAppointment;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public Appointment getAppointment(Long appointmentId) {
        if (appointmentId == null) {
            log.error("Appointment ID cannot be null");
            throw new IllegalArgumentException("Appointment ID is required");
        }

        try {
            log.info("Fetching appointment with ID: {}", appointmentId);
            return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));
        } catch (Exception e) {
            log.error("Error fetching appointment: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch appointment: " + e.getMessage());
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        if (doctorId == null) {
            log.error("Doctor ID cannot be null");
            throw new IllegalArgumentException("Doctor ID is required");
        }

        try {
            log.info("Fetching appointments for doctor ID: {}", doctorId);
            Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
            return appointmentRepository.findByDoctor(doctor);
        } catch (Exception e) {
            log.error("Error fetching doctor appointments: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch doctor appointments: " + e.getMessage());
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public List<Appointment> getAllAppointments() {
        try {
            log.info("Fetching all appointments");
            return appointmentRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all appointments: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch appointments: " + e.getMessage());
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, Appointment.AppointmentStatus status) {
        if (appointmentId == null || status == null) {
            log.error("Appointment ID and status cannot be null");
            throw new IllegalArgumentException("Appointment ID and status are required");
        }

        try {
            log.info("Updating appointment status for ID: {} to {}", appointmentId, status);
            Appointment appointment = getAppointment(appointmentId);

            if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
                log.error("Cannot update status of cancelled appointment");
                throw new IllegalStateException("Cannot update status of cancelled appointment");
            }

            appointment.setStatus(status);
            return appointmentRepository.save(appointment);
        } catch (Exception e) {
            log.error("Error updating appointment status: {}", e.getMessage());
            throw new RuntimeException("Failed to update appointment status: " + e.getMessage());
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        if (appointmentId == null) {
            log.error("Appointment ID cannot be null");
            throw new IllegalArgumentException("Appointment ID is required");
        }

        try {
            log.info("Cancelling appointment with ID: {}", appointmentId);
            Appointment appointment = getAppointment(appointmentId);

            if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                log.error("Cannot cancel completed appointment");
                throw new IllegalStateException("Cannot cancel completed appointment");
            }

            appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
            appointmentRepository.save(appointment);
            log.info("Appointment cancelled successfully");
        } catch (Exception e) {
            log.error("Error cancelling appointment: {}", e.getMessage());
            throw new RuntimeException("Failed to cancel appointment: " + e.getMessage());
        }
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newAppointmentTime) {
        if (appointmentId == null || newAppointmentTime == null) {
            log.error("Appointment ID and new appointment time cannot be null");
            throw new IllegalArgumentException("Appointment ID and new appointment time are required");
        }

        if (newAppointmentTime.isBefore(LocalDateTime.now())) {
            log.error("Cannot reschedule appointment to a past time");
            throw new IllegalArgumentException("New appointment time must be in the future");
        }

        try {
            log.info("Rescheduling appointment ID: {} to new time: {}", appointmentId, newAppointmentTime);

            // Get the existing appointment
            Appointment appointment = getAppointment(appointmentId);

            // Check if appointment can be rescheduled
            if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
                log.error("Cannot reschedule cancelled appointment");
                throw new IllegalStateException("Cannot reschedule cancelled appointment");
            }

            if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
                log.error("Cannot reschedule completed appointment");
                throw new IllegalStateException("Cannot reschedule completed appointment");
            }

            // Check for conflicts with the new time
            checkAppointmentConflicts(appointment.getDoctor().getId(), newAppointmentTime);

            // Update the appointment time
            appointment.setAppointmentTime(newAppointmentTime);


            Appointment rescheduledAppointment = appointmentRepository.save(appointment);
            log.info("Appointment rescheduled successfully to: {}", newAppointmentTime);
            return rescheduledAppointment;

        } catch (Exception e) {
            log.error("Error rescheduling appointment: {}", e.getMessage());
            throw new RuntimeException("Failed to reschedule appointment: " + e.getMessage());
        }
    }

    private void checkAppointmentConflicts(Long doctorId, LocalDateTime appointmentTime) {
        List<Appointment> conflictingAppointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
            doctorId,
            appointmentTime.minusMinutes(30),
            appointmentTime.plusMinutes(30)
        );

        if (!conflictingAppointments.isEmpty()) {
            throw new AppointmentConflictException("Doctor has another appointment at this time");
        }
    }
}
