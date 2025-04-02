package com.example.alertnotification.controller;

import com.example.alertnotification.model.WeatherAlert;
import com.example.alertnotification.service.AlertHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AlertControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertHistoryService alertHistoryService;

    @InjectMocks
    private AlertController alertController;

    private WeatherAlert testAlert1;
    private WeatherAlert testAlert2;
    private List<WeatherAlert> allAlerts;

    @BeforeEach
    public void setup() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();

        // Create test data
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

        allAlerts = Arrays.asList(testAlert1, testAlert2);
    }

    @Test
    public void testGetRecentAlerts() throws Exception {
        // Configure the mock service
        when(alertHistoryService.getRecentAlerts()).thenReturn(allAlerts);

        // Perform the HTTP GET request and validate the response
        mockMvc.perform(get("/api/alerts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].alertId", is("test-alert-1")))
                .andExpect(jsonPath("$[1].alertId", is("test-alert-2")));
    }

    @Test
    public void testGetAlertsByLocation() throws Exception {
        // Mock the service to return just one alert for a specific location
        List<WeatherAlert> locationAlerts = Arrays.asList(testAlert1);
        when(alertHistoryService.getAlertsByLocation(anyString())).thenReturn(locationAlerts);

        // Perform the HTTP GET request with location ID
        mockMvc.perform(get("/api/alerts/location/location-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alertId", is("test-alert-1")))
                .andExpect(jsonPath("$[0].locationId", is("location-1")));
    }

    @Test
    public void testGetAlertsByType() throws Exception {
        // Mock the service to return just one alert for a specific type
        List<WeatherAlert> typeAlerts = Arrays.asList(testAlert1);
        when(alertHistoryService.getAlertsByType(anyString())).thenReturn(typeAlerts);

        // Perform the HTTP GET request with alert type
        mockMvc.perform(get("/api/alerts/type/HIGH_TEMPERATURE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alertId", is("test-alert-1")))
                .andExpect(jsonPath("$[0].alertType", is("HIGH_TEMPERATURE")));
    }
}
