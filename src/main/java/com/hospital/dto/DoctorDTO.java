package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoctorDTO {
    private Long id;

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String email;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private Long departmentId;
}
