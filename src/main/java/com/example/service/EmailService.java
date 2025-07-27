package com.example.service;

import com.example.dto.EmailRequestDTO;
import com.example.dto.EmailResponseDTO;
import com.example.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Service
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Retryable(
        retryFor = {MailSendException.class, MessagingException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public EmailResponseDTO sendEmail(EmailRequestDTO emailRequest) {
        String emailId = UUID.randomUUID().toString();
        log.info("Attempting to send email with ID: {} to: {}", emailId, emailRequest.getTo());

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set basic email properties
            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(emailRequest.getBody(), emailRequest.isHtml());

            // Set CC if provided
            if (emailRequest.getCc() != null && !emailRequest.getCc().trim().isEmpty()) {
                helper.setCc(emailRequest.getCc());
            }

            // Set BCC if provided
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().trim().isEmpty()) {
                helper.setBcc(emailRequest.getBcc());
            }

            // Add attachments if provided
            if (emailRequest.getAttachmentPaths() != null && !emailRequest.getAttachmentPaths().isEmpty()) {
                for (String attachmentPath : emailRequest.getAttachmentPaths()) {
                    try {
                        File file = new File(attachmentPath);
                        if (file.exists() && file.isFile()) {
                            helper.addAttachment(file.getName(), file);
                            log.debug("Added attachment: {}", file.getName());
                        } else {
                            log.warn("Attachment file not found: {}", attachmentPath);
                        }
                    } catch (Exception e) {
                        log.error("Failed to add attachment: {}", attachmentPath, e);
                        // Continue with sending email without this attachment
                    }
                }
            }

            // Send the email
            mailSender.send(message);

            log.info("Email sent successfully with ID: {} to: {}", emailId, emailRequest.getTo());
            return EmailResponseDTO.success(emailId, emailRequest.getTo(), emailRequest.getSubject());

        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed for email ID: {}", emailId, e);
            throw new EmailException("Email authentication failed. Please check SMTP credentials.",
                e, emailRequest.getTo(), emailRequest.getSubject());
        } catch (MailSendException e) {
            log.error("Failed to send email with ID: {} due to send exception", emailId, e);
            throw new EmailException("Failed to send email. SMTP server error: " + e.getMessage(),
                e, emailRequest.getTo(), emailRequest.getSubject());
        } catch (MessagingException e) {
            log.error("Messaging error for email ID: {}", emailId, e);
            throw new EmailException("Email formatting error: " + e.getMessage(),
                e, emailRequest.getTo(), emailRequest.getSubject());
        } catch (MailException e) {
            log.error("General mail error for email ID: {}", emailId, e);
            throw new EmailException("Email service error: " + e.getMessage(),
                e, emailRequest.getTo(), emailRequest.getSubject());
        } catch (Exception e) {
            log.error("Unexpected error while sending email with ID: {}", emailId, e);
            throw new EmailException("Unexpected error occurred while sending email: " + e.getMessage(),
                e, emailRequest.getTo(), emailRequest.getSubject());
        }
    }

    public EmailResponseDTO sendSimpleEmail(String to, String subject, String body) {
        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);
        emailRequest.setHtml(false);

        return sendEmail(emailRequest);
    }

    public EmailResponseDTO sendHtmlEmail(String to, String subject, String htmlBody) {
        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(htmlBody);
        emailRequest.setHtml(true);

        return sendEmail(emailRequest);
    }
}
