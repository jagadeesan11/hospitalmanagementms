package com.example.exception;

public class EmailException extends RuntimeException {
    private String recipient;
    private String subject;

    public EmailException(String message) {
        super(message);
    }

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailException(String message, String recipient, String subject) {
        super(message);
        this.recipient = recipient;
        this.subject = subject;
    }

    public EmailException(String message, Throwable cause, String recipient, String subject) {
        super(message, cause);
        this.recipient = recipient;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }
}
