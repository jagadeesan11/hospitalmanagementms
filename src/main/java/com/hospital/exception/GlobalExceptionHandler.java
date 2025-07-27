package com.hospital.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    private final ErrorMessageResolver errorMessageResolver;

    public GlobalExceptionHandler(ErrorMessageResolver errorMessageResolver) {
        this.errorMessageResolver = errorMessageResolver;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.resource.notfound");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.NOT_FOUND.value()
        );
        log.error("Resource not found exception: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<ErrorResponse> handleAppointmentConflictException(
            AppointmentConflictException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.appointment.conflict");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.CONFLICT.value()
        );
        log.error("Appointment conflict: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.badrequest");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.BAD_REQUEST.value()
        );
        log.error("Invalid input: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.illegalstate");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.CONFLICT.value()
        );
        log.error("Invalid operation: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.validation");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                "Validation failed",
                error.getDetail(),
                HttpStatus.BAD_REQUEST.value()
        );

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
            response.addValidationError(fieldError.getField(), fieldError.getDefaultMessage())
        );

        log.error("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(
            EmailException ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.email.send");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        // Add additional context for email errors
        if (ex.getRecipient() != null) {
            response.addAdditionalInfo("recipient", ex.getRecipient());
        }
        if (ex.getSubject() != null) {
            response.addAdditionalInfo("subject", ex.getSubject());
        }

        log.error("Email sending failed for recipient: {}, subject: {}", ex.getRecipient(), ex.getSubject(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({StackOverflowError.class})
    public ResponseEntity<ErrorResponse> handleStackOverflowError(
            StackOverflowError ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.internal");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                "A recursive operation error occurred",
                error.getDetail(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        log.error("Stack overflow error occurred", ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        ErrorMessageResolver.ErrorDetail error = errorMessageResolver.getErrorDetail("error.internal");
        ErrorResponse response = ErrorResponse.of(
                error.getCode(),
                error.getTitle(),
                ex.getMessage(),
                error.getDetail(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
