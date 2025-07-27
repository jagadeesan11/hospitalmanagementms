package com.example.controller;

import com.example.dto.AppointmentDTO;
import com.example.dto.AppointmentStatusDTO;
import com.example.dto.RescheduleRequestDTO;
import com.example.entity.Appointment;
import com.example.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointment Management", description = "APIs for managing doctor appointments")
@Log4j2
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/doctor/{doctorId}/patient")
    @Operation(summary = "Create a new appointment with a doctor for a patient")
    public ResponseEntity<Appointment> createAppointment(
            @PathVariable Long doctorId,
            @RequestBody AppointmentDTO appointmentDTO) {
        log.info("Request received to create appointment with doctor ID: {}", doctorId);
        // Override the IDs from path variables if provided
        appointmentDTO.setDoctor(appointmentDTO.getDoctor());
        appointmentDTO.setPatient(appointmentDTO.getPatient());
        return ResponseEntity.ok(appointmentService.createAppointment(appointmentDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        log.info("Request received to fetch appointment with ID: {}", id);
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get all appointments for a doctor")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable Long doctorId) {
        log.info("Request received to fetch appointments for doctor ID: {}", doctorId);
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody AppointmentStatusDTO statusDTO) {
        log.info("Request received to update status for appointment ID: {} to {}", id, statusDTO.getStatus());
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, statusDTO.getStatus()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an appointment")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        log.info("Request received to cancel appointment with ID: {}", id);
        appointmentService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        log.info("Request received to fetch all appointments");
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment to a new time")
    public ResponseEntity<Appointment> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody RescheduleRequestDTO rescheduleRequest) {
        log.info("Request received to reschedule appointment ID: {} to new time: {}",
                id, rescheduleRequest.getNewAppointmentTime());

        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, rescheduleRequest.getNewAppointmentTime()));
    }
}
