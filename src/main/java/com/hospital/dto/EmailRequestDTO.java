package com.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class EmailRequestDTO {
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    @Email(message = "Invalid CC email format")
    private String cc;

    @Email(message = "Invalid BCC email format")
    private String bcc;

    @NotBlank(message = "Subject is required")
    @Size(max = 255, message = "Subject must not exceed 255 characters")
    private String subject;

    @NotBlank(message = "Message body is required")
    @Size(max = 5000, message = "Message body must not exceed 5000 characters")
    private String body;

    private boolean isHtml = false;

    private List<String> attachmentPaths;

    // Template-based email fields (optional)
    private String templateName;
    private Object templateData;
}
