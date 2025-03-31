package com.example.alertnotification.controller;

import com.example.alertnotification.model.WeatherAlert;
import com.example.alertnotification.service.AlertHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertHistoryService alertHistoryService;
    
    @GetMapping
    public ResponseEntity<List<WeatherAlert>> getRecentAlerts() {
        return ResponseEntity.ok(alertHistoryService.getRecentAlerts());
    }
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<WeatherAlert>> getAlertsByLocation(@PathVariable String locationId) {
        return ResponseEntity.ok(alertHistoryService.getAlertsByLocation(locationId));
    }
    
    @GetMapping("/type/{alertType}")
    public ResponseEntity<List<WeatherAlert>> getAlertsByType(@PathVariable String alertType) {
        return ResponseEntity.ok(alertHistoryService.getAlertsByType(alertType));
    }
}
