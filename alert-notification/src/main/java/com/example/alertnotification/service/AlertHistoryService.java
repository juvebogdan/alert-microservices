package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import com.example.alertnotification.repository.WeatherAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertHistoryService {

    // Inject the repository using constructor injection (handled by Lombok's @RequiredArgsConstructor)
    private final WeatherAlertRepository alertRepository;
    
    public void addAlert(WeatherAlert alert) {
        // Save the alert to the database
        alertRepository.save(alert);
    }
    
    public List<WeatherAlert> getRecentAlerts() {
        // Return the most recent 100 alerts
        return alertRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, 100));
    }
    
    public List<WeatherAlert> getAlertsByLocation(String locationId) {
        // Get alerts for a specific location
        return alertRepository.findByLocationIdOrderByTimestampDesc(locationId);
    }
    
    public List<WeatherAlert> getAlertsByType(String alertType) {
        // Get alerts of a specific type
        return alertRepository.findByAlertTypeOrderByTimestampDesc(alertType);
    }
}
