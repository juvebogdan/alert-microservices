package com.example.weatheranalyzer.service;

import com.example.weatheranalyzer.model.WeatherAlert;
import com.example.weatheranalyzer.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherDataConsumer {

    private final WeatherAlertProducer alertProducer;
    
    // Thresholds for alerts
    private static final double HIGH_TEMP_THRESHOLD = 35.0; // in Celsius
    private static final double HIGH_WIND_THRESHOLD = 20.0; // in m/s
    private static final double HEAVY_RAIN_THRESHOLD = 10.0; // in mm
    
    @KafkaListener(topics = "weather-data", groupId = "weather-analyzer-group")
    public void consume(WeatherData weatherData) {
        log.info("Received weather data: {}", weatherData);
        
        // Analyze weather data for extreme conditions
        analyzeTemperature(weatherData);
        analyzeWindSpeed(weatherData);
        analyzeRainfall(weatherData);
    }
    
    private void analyzeTemperature(WeatherData data) {
        if (data.getTemperature() > HIGH_TEMP_THRESHOLD) {
            WeatherAlert alert = new WeatherAlert(
                UUID.randomUUID().toString(),
                data.getLocationId(),
                data.getLocationName(),
                "HIGH_TEMPERATURE",
                "High temperature detected at " + data.getLocationName(),
                data.getTemperature(),
                "HIGH",
                LocalDateTime.now()
            );
            alertProducer.sendAlert(alert);
        }
    }
    
    private void analyzeWindSpeed(WeatherData data) {
        if (data.getWindSpeed() > HIGH_WIND_THRESHOLD) {
            WeatherAlert alert = new WeatherAlert(
                UUID.randomUUID().toString(),
                data.getLocationId(),
                data.getLocationName(),
                "HIGH_WIND",
                "High wind speed detected at " + data.getLocationName(),
                data.getWindSpeed(),
                "MEDIUM",
                LocalDateTime.now()
            );
            alertProducer.sendAlert(alert);
        }
    }
    
    private void analyzeRainfall(WeatherData data) {
        if (data.getPrecipitation() > HEAVY_RAIN_THRESHOLD) {
            WeatherAlert alert = new WeatherAlert(
                UUID.randomUUID().toString(),
                data.getLocationId(),
                data.getLocationName(),
                "HEAVY_RAIN",
                "Heavy rainfall detected at " + data.getLocationName(),
                data.getPrecipitation(),
                "MEDIUM",
                LocalDateTime.now()
            );
            alertProducer.sendAlert(alert);
        }
    }
}
