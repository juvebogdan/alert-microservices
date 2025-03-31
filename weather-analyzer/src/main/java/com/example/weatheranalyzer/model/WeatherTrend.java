package com.example.weatheranalyzer.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Tracks weather trends for a specific location over time
 */
@Getter
public class WeatherTrend {
    private final String locationName;
    
    // Tracking the last few temperature readings
    private final Queue<WeatherReading> recentReadings = new LinkedList<>();
    private double temperatureChange = 0.0;
    
    // Maximum readings to store for trend analysis
    private static final int MAX_READINGS = 10;
    
    public WeatherTrend(String locationName) {
        this.locationName = locationName;
    }
    
    /**
     * Update trend data with new weather data
     * @return true if we have enough data to calculate a meaningful trend
     */
    public boolean updateData(WeatherData data) {
        WeatherReading newReading = new WeatherReading(
            data.getTemperature(),
            data.getTimestamp()
        );
        
        // Add the new reading
        recentReadings.add(newReading);
        
        // Maintain the maximum size of our tracking queue
        if (recentReadings.size() > MAX_READINGS) {
            recentReadings.poll(); // Remove the oldest reading
        }
        
        // Calculate temperature change in the last 30 minutes
        calculateRecentTemperatureChange();
        
        // Return true if we have at least two readings
        return recentReadings.size() >= 2;
    }
    
    private void calculateRecentTemperatureChange() {
        if (recentReadings.size() < 2) {
            temperatureChange = 0.0;
            return;
        }
        
        // Find the oldest reading within the last 30 minutes
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        WeatherReading oldestRecentReading = null;
        
        for (WeatherReading reading : recentReadings) {
            if (reading.timestamp.isAfter(thirtyMinutesAgo)) {
                if (oldestRecentReading == null || reading.timestamp.isBefore(oldestRecentReading.timestamp)) {
                    oldestRecentReading = reading;
                }
            }
        }
        
        // If we don't have any readings in the last 30 minutes, use the oldest reading we have
        if (oldestRecentReading == null) {
            oldestRecentReading = recentReadings.peek();
        }
        
        // Get the newest reading
        WeatherReading newestReading = null;
        for (WeatherReading reading : recentReadings) {
            if (newestReading == null || reading.timestamp.isAfter(newestReading.timestamp)) {
                newestReading = reading;
            }
        }
        
        // Calculate temperature change
        if (oldestRecentReading != null && newestReading != null) {
            temperatureChange = newestReading.temperature - oldestRecentReading.temperature;
        }
    }
    
    /**
     * Inner class to store a weather reading with timestamp
     */
    private static class WeatherReading {
        private final double temperature;
        private final LocalDateTime timestamp;
        
        public WeatherReading(double temperature, LocalDateTime timestamp) {
            this.temperature = temperature;
            this.timestamp = timestamp;
        }
    }
}
