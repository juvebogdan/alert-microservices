package com.example.weatheranalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    private String locationId;
    private String locationName;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String windDirection;
    private double precipitation;
    private LocalDateTime timestamp;
}
