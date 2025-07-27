package com.example.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String code;
    private String error;
    private String message;
    private String details;
    private Map<String, String> validationErrors;
    private Map<String, String> additionalInfo;

    public static ErrorResponse of(String code, String error, String message, String details, int status) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status);
        response.setCode(code);
        response.setError(error);
        response.setMessage(message);
        response.setDetails(details);
        response.setValidationErrors(new HashMap<>());
        response.setAdditionalInfo(new HashMap<>());
        return response;
    }

    public void addValidationError(String field, String message) {
        if (validationErrors == null) {
            validationErrors = new HashMap<>();
        }
        validationErrors.put(field, message);
    }

    public void addAdditionalInfo(String key, String value) {
        if (additionalInfo == null) {
            additionalInfo = new HashMap<>();
        }
        additionalInfo.put(key, value);
    }
}
