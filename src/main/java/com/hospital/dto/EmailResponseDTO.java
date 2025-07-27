package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponseDTO {
    private boolean success;
    private String message;
    private String emailId;
    private LocalDateTime sentAt;
    private String recipient;
    private String subject;

    public static EmailResponseDTO success(String emailId, String recipient, String subject) {
        return new EmailResponseDTO(true, "Email sent successfully", emailId, LocalDateTime.now(), recipient, subject);
    }

    public static EmailResponseDTO failure(String message, String recipient, String subject) {
        return new EmailResponseDTO(false, message, null, null, recipient, subject);
    }
}
