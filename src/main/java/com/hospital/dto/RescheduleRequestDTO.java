package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleRequestDTO {
    @NotNull(message = "New appointment time is required")
    private LocalDateTime newAppointmentTime;
}
