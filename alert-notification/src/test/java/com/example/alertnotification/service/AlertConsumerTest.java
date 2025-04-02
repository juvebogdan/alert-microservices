package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AlertConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private AlertHistoryService alertHistoryService;

    @InjectMocks
    private AlertConsumer alertConsumer;

    private WeatherAlert highSeverityAlert;
    private WeatherAlert mediumSeverityAlert;
    private WeatherAlert lowSeverityAlert;

    @BeforeEach
    void setUp() {
        // Create test alerts with different severity levels
        highSeverityAlert = new WeatherAlert(
                UUID.randomUUID().toString(),
                "location-1",
                "Test Location",
                "HIGH_TEMPERATURE",
                "High temperature alert",
                38.5,
                "HIGH",
                LocalDateTime.now()
        );

        mediumSeverityAlert = new WeatherAlert(
                UUID.randomUUID().toString(),
                "location-2",
                "Another Location",
                "HIGH_WIND",
                "High wind alert",
                25.0,
                "MEDIUM",
                LocalDateTime.now()
        );

        lowSeverityAlert = new WeatherAlert(
                UUID.randomUUID().toString(),
                "location-3",
                "Third Location",
                "LIGHT_RAIN",
                "Light rain expected",
                10.0,
                "LOW",
                LocalDateTime.now()
        );
    }

    @Test
    void testHighSeverityAlertProcessing() {
        // Call the method being tested
        alertConsumer.consumeAlert(highSeverityAlert);

        // Verify that the alert was added to history
        verify(alertHistoryService, times(1)).addAlert(highSeverityAlert);
        
        // Verify that all notification types were sent for HIGH severity
        verify(notificationService, times(1)).sendEmailNotification(highSeverityAlert);
        verify(notificationService, times(1)).sendSmsNotification(highSeverityAlert);
        verify(notificationService, times(1)).sendPushNotification(highSeverityAlert);
    }

    @Test
    void testMediumSeverityAlertProcessing() {
        // Call the method being tested
        alertConsumer.consumeAlert(mediumSeverityAlert);

        // Verify that the alert was added to history
        verify(alertHistoryService, times(1)).addAlert(mediumSeverityAlert);
        
        // Verify that only email and push notifications were sent for MEDIUM severity
        verify(notificationService, times(1)).sendEmailNotification(mediumSeverityAlert);
        verify(notificationService, times(0)).sendSmsNotification(mediumSeverityAlert);
        verify(notificationService, times(1)).sendPushNotification(mediumSeverityAlert);
    }

    @Test
    void testLowSeverityAlertProcessing() {
        // Call the method being tested
        alertConsumer.consumeAlert(lowSeverityAlert);

        // Verify that the alert was added to history
        verify(alertHistoryService, times(1)).addAlert(lowSeverityAlert);
        
        // Verify that only email notification was sent for LOW severity
        verify(notificationService, times(1)).sendEmailNotification(lowSeverityAlert);
        verify(notificationService, times(0)).sendSmsNotification(lowSeverityAlert);
        verify(notificationService, times(0)).sendPushNotification(lowSeverityAlert);
    }
}
