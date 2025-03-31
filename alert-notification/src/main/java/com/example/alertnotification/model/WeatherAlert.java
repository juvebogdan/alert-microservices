package com.example.alertnotification.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {
    @Id
    private String alertId;
    
    private String locationId;
    private String locationName;
    private String alertType;
    private String alertMessage;
    private double alertValue;
    private String severity; // HIGH, MEDIUM, LOW
    private LocalDateTime timestamp;
}
