package com.example.controller;

import com.example.dto.EmailRequestDTO;
import com.example.dto.EmailResponseDTO;
import com.example.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emails")
@Log4j2
@Tag(name = "Email Management", description = "APIs for sending emails via SMTP")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send email", description = "Send an email with optional attachments and HTML content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email request"),
        @ApiResponse(responseCode = "500", description = "Email service error")
    })
    public ResponseEntity<EmailResponseDTO> sendEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        log.info("Received email request to: {}, subject: {}", emailRequest.getTo(), emailRequest.getSubject());

        EmailResponseDTO response = emailService.sendEmail(emailRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send/simple")
    @Operation(summary = "Send simple text email", description = "Send a simple text email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email parameters"),
        @ApiResponse(responseCode = "500", description = "Email service error")
    })
    public ResponseEntity<EmailResponseDTO> sendSimpleEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {

        log.info("Received simple email request to: {}, subject: {}", to, subject);

        EmailResponseDTO response = emailService.sendSimpleEmail(to, subject, body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send/html")
    @Operation(summary = "Send HTML email", description = "Send an HTML formatted email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid email parameters"),
        @ApiResponse(responseCode = "500", description = "Email service error")
    })
    public ResponseEntity<EmailResponseDTO> sendHtmlEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String htmlBody) {

        log.info("Received HTML email request to: {}, subject: {}", to, subject);

        EmailResponseDTO response = emailService.sendHtmlEmail(to, subject, htmlBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send/appointment-confirmation")
    @Operation(summary = "Send appointment confirmation email", description = "Send appointment confirmation email to patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Confirmation email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid appointment data"),
        @ApiResponse(responseCode = "500", description = "Email service error")
    })
    public ResponseEntity<EmailResponseDTO> sendAppointmentConfirmation(
            @RequestParam String patientEmail,
            @RequestParam String patientName,
            @RequestParam String doctorName,
            @RequestParam String appointmentDate,
            @RequestParam String appointmentTime,
            @RequestParam String hospitalName) {

        log.info("Sending appointment confirmation email to: {}", patientEmail);

        String subject = "Appointment Confirmation - " + hospitalName;
        String htmlBody = buildAppointmentConfirmationHtml(patientName, doctorName, appointmentDate, appointmentTime, hospitalName);

        EmailResponseDTO response = emailService.sendHtmlEmail(patientEmail, subject, htmlBody);
        return ResponseEntity.ok(response);
    }

    private String buildAppointmentConfirmationHtml(String patientName, String doctorName,
                                                   String appointmentDate, String appointmentTime, String hospitalName) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #2c3e50; margin-bottom: 10px;">🏥 Appointment Confirmation</h1>
                        <h2 style="color: #3498db; margin-top: 0;">%s</h2>
                    </div>
                    
                    <div style="background-color: #ecf0f1; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                        <p style="font-size: 16px; margin: 0 0 15px 0;"><strong>Dear %s,</strong></p>
                        <p style="font-size: 14px; line-height: 1.6; margin: 0;">
                            Your appointment has been confirmed! Please find the details below:
                        </p>
                    </div>
                    
                    <div style="margin: 20px 0;">
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 12px; border: 1px solid #dee2e6; font-weight: bold; width: 30%%;">Doctor:</td>
                                <td style="padding: 12px; border: 1px solid #dee2e6;">Dr. %s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px; border: 1px solid #dee2e6; font-weight: bold;">Date:</td>
                                <td style="padding: 12px; border: 1px solid #dee2e6;">%s</td>
                            </tr>
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 12px; border: 1px solid #dee2e6; font-weight: bold;">Time:</td>
                                <td style="padding: 12px; border: 1px solid #dee2e6;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px; border: 1px solid #dee2e6; font-weight: bold;">Hospital:</td>
                                <td style="padding: 12px; border: 1px solid #dee2e6;">%s</td>
                            </tr>
                        </table>
                    </div>
                    
                    <div style="background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; border-radius: 8px; margin: 20px 0;">
                        <p style="margin: 0; color: #155724; font-weight: bold;">📋 Important Reminders:</p>
                        <ul style="color: #155724; margin: 10px 0 0 20px; padding: 0;">
                            <li>Please arrive 15 minutes before your appointment time</li>
                            <li>Bring a valid ID and insurance card</li>
                            <li>If you need to reschedule, please call us at least 24 hours in advance</li>
                        </ul>
                    </div>
                    
                    <div style="text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6;">
                        <p style="margin: 0; color: #6c757d; font-size: 12px;">
                            This is an automated message. Please do not reply to this email.
                        </p>
                        <p style="margin: 5px 0 0 0; color: #6c757d; font-size: 12px;">
                            © 2025 %s. All rights reserved.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(hospitalName, patientName, doctorName, appointmentDate, appointmentTime, hospitalName, hospitalName);
    }
}
