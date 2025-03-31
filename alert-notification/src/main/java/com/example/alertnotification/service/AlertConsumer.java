package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertConsumer {

    private final NotificationService notificationService;
    private final AlertHistoryService alertHistoryService;
    
    @KafkaListener(topics = "weather-alerts", groupId = "alert-notification-group")
    public void consumeAlert(WeatherAlert alert) {
        log.info("Received weather alert: {}", alert);
        
        // Add to alert history
        alertHistoryService.addAlert(alert);
        
        // Send notifications based on severity
        if ("HIGH".equals(alert.getSeverity())) {
            // For high severity alerts, send all notification types
            notificationService.sendEmailNotification(alert);
            notificationService.sendSmsNotification(alert);
            notificationService.sendPushNotification(alert);
        } 
        else if ("MEDIUM".equals(alert.getSeverity())) {
            // For medium severity, just send email and push
            notificationService.sendEmailNotification(alert);
            notificationService.sendPushNotification(alert);
        }
        else {
            // For low severity, only send email
            notificationService.sendEmailNotification(alert);
        }
    }
}
