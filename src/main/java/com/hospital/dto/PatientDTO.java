package com.hospital.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
    private String bloodGroup;
    private Long hospitalId;
}
