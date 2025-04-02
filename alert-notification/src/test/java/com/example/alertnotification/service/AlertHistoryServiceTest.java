package com.example.alertnotification.service;

import com.example.alertnotification.model.WeatherAlert;
import com.example.alertnotification.repository.WeatherAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertHistoryServiceTest {

    @Mock
    private WeatherAlertRepository alertRepository;

    @InjectMocks
    private AlertHistoryService alertHistoryService;

    private WeatherAlert testAlert1;
    private WeatherAlert testAlert2;

    @BeforeEach
    public void setup() {
        // Create some test alert data to use in multiple tests
        testAlert1 = new WeatherAlert(
                "test-alert-1",
                "location-1",
                "Test Location",
                "HIGH_TEMPERATURE",
                "High temperature alert",
                35.5,
                "HIGH",
                LocalDateTime.now().minusHours(1)
        );

        testAlert2 = new WeatherAlert(
                "test-alert-2",
                "location-2",
                "Another Location",
                "HIGH_WIND",
                "High wind alert",
                25.0,
                "MEDIUM",
                LocalDateTime.now()
        );
    }

    @Test
    public void testAddAlert() {
        // Configure the mock repository to return the alert when saved
        when(alertRepository.save(any(WeatherAlert.class))).thenReturn(testAlert1);

        // Execute the service method
        alertHistoryService.addAlert(testAlert1);

        // Verify that the repository's save method was called once with the correct alert
        verify(alertRepository, times(1)).save(testAlert1);
    }

    @Test
    public void testGetRecentAlerts() {
        // Configure the mock repository to return our test alerts when findAllByOrderByTimestampDesc is called
        List<WeatherAlert> alerts = Arrays.asList(testAlert2, testAlert1); // Newer first
        when(alertRepository.findAllByOrderByTimestampDesc(any(Pageable.class))).thenReturn(alerts);

        // Execute the service method
        List<WeatherAlert> result = alertHistoryService.getRecentAlerts();

        // Verify the repository method was called
        verify(alertRepository, times(1)).findAllByOrderByTimestampDesc(any(PageRequest.class));

        // Verify the results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testAlert2.getAlertId(), result.get(0).getAlertId());
        assertEquals(testAlert1.getAlertId(), result.get(1).getAlertId());
    }

    @Test
    public void testGetAlertsByLocation() {
        // Configure the mock repository to return one alert for our test location
        List<WeatherAlert> locationAlerts = Arrays.asList(testAlert1);
        when(alertRepository.findByLocationIdOrderByTimestampDesc(anyString())).thenReturn(locationAlerts);

        // Execute the service method
        List<WeatherAlert> result = alertHistoryService.getAlertsByLocation("location-1");

        // Verify the repository method was called with the correct location ID
        verify(alertRepository, times(1)).findByLocationIdOrderByTimestampDesc("location-1");

        // Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAlert1.getAlertId(), result.get(0).getAlertId());
    }

    @Test
    public void testGetAlertsByType() {
        // Configure the mock repository to return one alert for our test alert type
        List<WeatherAlert> typeAlerts = Arrays.asList(testAlert1);
        when(alertRepository.findByAlertTypeOrderByTimestampDesc(anyString())).thenReturn(typeAlerts);

        // Execute the service method
        List<WeatherAlert> result = alertHistoryService.getAlertsByType("HIGH_TEMPERATURE");

        // Verify the repository method was called with the correct alert type
        verify(alertRepository, times(1)).findByAlertTypeOrderByTimestampDesc("HIGH_TEMPERATURE");

        // Verify the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAlert1.getAlertId(), result.get(0).getAlertId());
    }
}
