package com.hospital.dto;

import com.hospital.entity.Appointment.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentStatusDTO {
    private AppointmentStatus status;
}
