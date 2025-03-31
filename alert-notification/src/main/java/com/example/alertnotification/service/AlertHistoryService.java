package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AlertHistoryService {

    // In-memory storage for alerts (in a real app, this would be a database)
    private final List<WeatherAlert> alertHistory = new CopyOnWriteArrayList<>();
    
    public void addAlert(WeatherAlert alert) {
        alertHistory.add(alert);
        // Keep only the latest 100 alerts
        if (alertHistory.size() > 100) {
            alertHistory.remove(0);
        }
    }
    
    public List<WeatherAlert> getRecentAlerts() {
        // Return alerts in reverse chronological order (newest first)
        List<WeatherAlert> recentAlerts = new ArrayList<>(alertHistory);
        Collections.reverse(recentAlerts);
        return recentAlerts;
    }
    
    public List<WeatherAlert> getAlertsByLocation(String locationId) {
        // Filter alerts by location
        return alertHistory.stream()
                .filter(alert -> alert.getLocationId().equals(locationId))
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()))
                .toList();
    }
    
    public List<WeatherAlert> getAlertsByType(String alertType) {
        // Filter alerts by type
        return alertHistory.stream()
                .filter(alert -> alert.getAlertType().equals(alertType))
                .sorted((a1, a2) -> a2.getTimestamp().compareTo(a1.getTimestamp()))
                .toList();
    }
}
