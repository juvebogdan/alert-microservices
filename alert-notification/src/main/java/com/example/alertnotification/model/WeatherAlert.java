package com.example.alertnotification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {
    private String alertId;
    private String locationId;
    private String locationName;
    private String alertType;
    private String alertMessage;
    private double alertValue;
    private String severity; // HIGH, MEDIUM, LOW
    private LocalDateTime timestamp;
}
