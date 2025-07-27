package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailConfig {
    private String fromName = "Hospital Management System";
    private Retry retry = new Retry();

    @Data
    public static class Retry {
        private boolean enabled = true;
        private int maxAttempts = 3;
        private long delay = 2000;
        private double multiplier = 2.0;
    }
}
