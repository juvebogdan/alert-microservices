package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class NotificationService {

    // Alert type descriptions for more informative notifications
    private static final Map<String, String> ALERT_DESCRIPTIONS = new HashMap<>();
    
    static {
        // Temperature alerts
        ALERT_DESCRIPTIONS.put("HIGH_TEMPERATURE", "High temperature conditions may cause discomfort and pose health risks to vulnerable individuals.");
        ALERT_DESCRIPTIONS.put("EXTREME_HIGH_TEMPERATURE", "Extreme heat can cause heat stroke, dehydration, and is dangerous for everyone, especially the elderly and children.");
        ALERT_DESCRIPTIONS.put("LOW_TEMPERATURE", "Low temperatures may cause cold-related discomfort. Please dress warmly when outdoors.");
        ALERT_DESCRIPTIONS.put("EXTREME_LOW_TEMPERATURE", "Extreme cold can cause frostbite and hypothermia. Avoid outdoor activities if possible.");
        ALERT_DESCRIPTIONS.put("RAPID_TEMPERATURE_CHANGE", "Rapid temperature changes can trigger health issues for those with respiratory conditions.");
        
        // Wind alerts
        ALERT_DESCRIPTIONS.put("HIGH_WIND", "High winds can make driving difficult and may cause minor damage to structures.");
        ALERT_DESCRIPTIONS.put("EXTREME_WIND", "Dangerous wind conditions may cause significant property damage. Secure loose objects and avoid travel.");
        
        // Precipitation alerts
        ALERT_DESCRIPTIONS.put("HEAVY_RAINFALL", "Heavy rain may cause local flooding and difficult travel conditions.");
        ALERT_DESCRIPTIONS.put("EXTREME_RAINFALL", "Extreme rainfall may cause flooding and dangerous travel conditions. Avoid flood-prone areas.");
        
        // Composite conditions
        ALERT_DESCRIPTIONS.put("TROPICAL_STORM_CONDITIONS", "Combination of strong winds and heavy rain may cause flooding, power outages, and property damage.");
    }

    public void sendEmailNotification(WeatherAlert alert) {
        // In a real application, this would connect to an email service
        log.info("Sending email notification for alert: {}", alert);
        
        String description = ALERT_DESCRIPTIONS.getOrDefault(
            alert.getAlertType(), 
            "Weather conditions require attention."
        );
        
        String emailContent = String.format(
            "WEATHER ALERT - %s - %s\n\n" +
            "Location: %s\n" +
            "Alert Type: %s\n" +
            "Message: %s\n" +
            "Severity: %s\n\n" +
            "Description: %s\n\n" +
            "Please take appropriate precautions.",
            alert.getSeverity(),
            alert.getAlertType(),
            alert.getLocationName(),
            alert.getAlertType(),
            alert.getAlertMessage(),
            alert.getSeverity(),
            description
        );
        
        log.info("Email content: \n{}", emailContent);
        
        // In a production environment, you would use:
        // - Spring Mail with SMTP
        // - A third-party service like SendGrid, Mailgun, or AWS SES
        // - A message queue for reliable delivery
    }
    
    public void sendSmsNotification(WeatherAlert alert) {
        // In a real application, this would connect to an SMS gateway
        log.info("Sending SMS notification for alert: {}", alert);
        
        String smsContent = String.format(
            "URGENT %s ALERT: %s - %s",
            alert.getAlertType(),
            alert.getLocationName(),
            alert.getAlertMessage()
        );
        
        log.info("SMS content: {}", smsContent);
        
        // In a production environment, you would use:
        // - Twilio API
        // - Amazon SNS
        // - A telecom gateway API
    }
    
    public void sendPushNotification(WeatherAlert alert) {
        // In a real application, this would send to a push notification service
        log.info("Sending push notification for alert: {}", alert);
        
        String pushContent = String.format(
            "%s - %s - %s",
            alert.getSeverity(),
            alert.getAlertType(),
            alert.getAlertMessage()
        );
        
        log.info("Push content: {}", pushContent);
        
        // In a production environment, you would use:
        // - Firebase Cloud Messaging
        // - Apple Push Notification Service
        // - Web Push API
    }
}
