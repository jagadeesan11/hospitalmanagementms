package com.example.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true, value = {"hibernateLazyInitializer", "handler"})
public abstract class AppointmentMixin {
    // Removed the problematic JsonCreator that was causing object ID conflicts
}
