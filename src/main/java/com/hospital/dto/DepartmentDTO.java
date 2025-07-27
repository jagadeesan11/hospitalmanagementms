package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;

    private Long blockId;
}
