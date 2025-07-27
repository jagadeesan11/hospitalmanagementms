package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlockDTO {
    private Long id;

    @NotBlank(message = "Block name is required")
    private String name;

    private String description;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;
}
