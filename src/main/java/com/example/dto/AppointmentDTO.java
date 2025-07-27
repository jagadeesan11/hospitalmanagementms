package com.example.dto;

import com.example.entity.Appointment;
import com.example.entity.Doctor;
import com.example.entity.Patient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;

    @NotNull(message = "Doctor ID is required")
    private Doctor doctor;

    //@NotNull(message = "Patient ID is required")
    private Patient patient;

    @NotNull(message = "Appointment time is required")
    private LocalDateTime appointmentTime;

    private Appointment.AppointmentStatus status = Appointment.AppointmentStatus.SCHEDULED;
}
