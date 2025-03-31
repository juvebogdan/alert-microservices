package com.example.weatheranalyzer.service;

import com.example.weatheranalyzer.model.WeatherAlert;
import com.example.weatheranalyzer.model.WeatherData;
import com.example.weatheranalyzer.model.WeatherTrend;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnhancedWeatherAnalyzer {

    private final WeatherAlertProducer alertProducer;
    
    // Cache for tracking weather trends by location
    private final Map<String, WeatherTrend> weatherTrends = new ConcurrentHashMap<>();
    
    // Alert thresholds
    private static final double HIGH_TEMP_THRESHOLD = 32.0; // in Celsius
    private static final double EXTREME_TEMP_THRESHOLD = 38.0; // in Celsius
    private static final double LOW_TEMP_THRESHOLD = 0.0; // in Celsius
    private static final double EXTREME_LOW_TEMP_THRESHOLD = -10.0; // in Celsius
    
    private static final double HIGH_WIND_THRESHOLD = 15.0; // in m/s (≈ 54 km/h)
    private static final double EXTREME_WIND_THRESHOLD = 25.0; // in m/s (≈ 90 km/h)
    
    private static final double HEAVY_RAIN_THRESHOLD = 5.0; // in mm/hour
    private static final double EXTREME_RAIN_THRESHOLD = 15.0; // in mm/hour
    
    private static final double RAPID_TEMP_CHANGE_THRESHOLD = 5.0; // Celsius within 30 minutes
    
    /**
     * Analyze incoming weather data for potential alert conditions
     */
    public void analyzeWeatherData(WeatherData data) {
        // Get or create trend data for this location
        WeatherTrend trend = weatherTrends.computeIfAbsent(
            data.getLocationId(),
            id -> new WeatherTrend(data.getLocationName())
        );
        
        // Update the trend with latest data
        boolean hasTrendData = trend.updateData(data);
        
        // Analyze for different alert conditions
        analyzeTemperature(data, trend, hasTrendData);
        analyzeWindSpeed(data);
        analyzeRainfall(data);
        
        // Advanced: Analyze combinations of weather factors
        analyzeTropicalStormConditions(data);
    }
    
    private void analyzeTemperature(WeatherData data, WeatherTrend trend, boolean hasTrendData) {
        // Check for extreme high temperature
        if (data.getTemperature() > EXTREME_TEMP_THRESHOLD) {
            createAndSendAlert(
                data,
                "EXTREME_HIGH_TEMPERATURE",
                "Extreme heat alert: " + data.getTemperature() + "°C at " + data.getLocationName(),
                data.getTemperature(),
                "HIGH"
            );
        }
        // Check for high temperature
        else if (data.getTemperature() > HIGH_TEMP_THRESHOLD) {
            createAndSendAlert(
                data,
                "HIGH_TEMPERATURE",
                "High temperature alert: " + data.getTemperature() + "°C at " + data.getLocationName(),
                data.getTemperature(),
                "MEDIUM"
            );
        }
        // Check for extreme low temperature
        else if (data.getTemperature() < EXTREME_LOW_TEMP_THRESHOLD) {
            createAndSendAlert(
                data,
                "EXTREME_LOW_TEMPERATURE",
                "Extreme cold alert: " + data.getTemperature() + "°C at " + data.getLocationName(),
                data.getTemperature(),
                "HIGH"
            );
        }
        // Check for low temperature
        else if (data.getTemperature() < LOW_TEMP_THRESHOLD) {
            createAndSendAlert(
                data,
                "LOW_TEMPERATURE",
                "Low temperature alert: " + data.getTemperature() + "°C at " + data.getLocationName(),
                data.getTemperature(),
                "MEDIUM"
            );
        }
        
        // Check for rapid temperature change if we have trend data
        if (hasTrendData && Math.abs(trend.getTemperatureChange()) > RAPID_TEMP_CHANGE_THRESHOLD) {
            createAndSendAlert(
                data,
                "RAPID_TEMPERATURE_CHANGE",
                String.format("Rapid temperature change of %.1f°C in the last 30 minutes at %s", 
                    trend.getTemperatureChange(), data.getLocationName()),
                Math.abs(trend.getTemperatureChange()),
                "MEDIUM"
            );
        }
    }
    
    private void analyzeWindSpeed(WeatherData data) {
        if (data.getWindSpeed() > EXTREME_WIND_THRESHOLD) {
            createAndSendAlert(
                data,
                "EXTREME_WIND",
                "Dangerous wind speeds of " + data.getWindSpeed() + " m/s detected at " + data.getLocationName(),
                data.getWindSpeed(),
                "HIGH"
            );
        }
        else if (data.getWindSpeed() > HIGH_WIND_THRESHOLD) {
            createAndSendAlert(
                data,
                "HIGH_WIND",
                "High wind speed of " + data.getWindSpeed() + " m/s detected at " + data.getLocationName(),
                data.getWindSpeed(),
                "MEDIUM"
            );
        }
    }
    
    private void analyzeRainfall(WeatherData data) {
        if (data.getPrecipitation() > EXTREME_RAIN_THRESHOLD) {
            createAndSendAlert(
                data,
                "EXTREME_RAINFALL",
                "Extreme rainfall of " + data.getPrecipitation() + " mm/h detected at " + data.getLocationName(),
                data.getPrecipitation(),
                "HIGH"
            );
        }
        else if (data.getPrecipitation() > HEAVY_RAIN_THRESHOLD) {
            createAndSendAlert(
                data,
                "HEAVY_RAINFALL",
                "Heavy rainfall of " + data.getPrecipitation() + " mm/h detected at " + data.getLocationName(),
                data.getPrecipitation(),
                "MEDIUM"
            );
        }
    }
    
    private void analyzeTropicalStormConditions(WeatherData data) {
        // Combination of high wind and heavy rain indicates possible tropical storm
        if (data.getWindSpeed() > HIGH_WIND_THRESHOLD && data.getPrecipitation() > HEAVY_RAIN_THRESHOLD) {
            createAndSendAlert(
                data,
                "TROPICAL_STORM_CONDITIONS",
                "Potential storm conditions detected at " + data.getLocationName() + 
                    ": Wind " + data.getWindSpeed() + " m/s, Rain " + data.getPrecipitation() + " mm/h",
                data.getWindSpeed(), // Use wind speed as primary value
                "HIGH"
            );
        }
    }
    
    private void createAndSendAlert(WeatherData data, String alertType, String message, 
                                  double alertValue, String severity) {
        WeatherAlert alert = new WeatherAlert(
            UUID.randomUUID().toString(),
            data.getLocationId(),
            data.getLocationName(),
            alertType,
            message,
            alertValue,
            severity,
            LocalDateTime.now()
        );
        
        log.info("Generating weather alert: {}", alert);
        alertProducer.sendAlert(alert);
    }
}
