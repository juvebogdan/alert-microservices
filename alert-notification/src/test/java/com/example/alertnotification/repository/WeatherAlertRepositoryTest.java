package com.example.alertnotification.repository;

import com.example.alertnotification.model.WeatherAlert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
public class WeatherAlertRepositoryTest {

    @Autowired
    private WeatherAlertRepository alertRepository;

    @Test
    public void testFindByLocationIdOrderByTimestampDesc() {
        // Create test data
        WeatherAlert alert1 = new WeatherAlert(
                "test-alert-1",
                "location-1",
                "Test Location",
                "HIGH_TEMPERATURE",
                "High temperature alert",
                35.5,
                "HIGH",
                LocalDateTime.now().minusHours(1)
        );

        WeatherAlert alert2 = new WeatherAlert(
                "test-alert-2",
                "location-1",
                "Test Location",
                "EXTREME_HIGH_TEMPERATURE",
                "Extreme temperature alert",
                40.0,
                "HIGH",
                LocalDateTime.now()
        );

        WeatherAlert alert3 = new WeatherAlert(
                "test-alert-3",
                "location-2",
                "Another Location",
                "HIGH_WIND",
                "High wind alert",
                25.0,
                "MEDIUM",
                LocalDateTime.now()
        );

        // Save test data
        alertRepository.saveAll(Arrays.asList(alert1, alert2, alert3));

        // Test the repository method
        List<WeatherAlert> locationAlerts = alertRepository.findByLocationIdOrderByTimestampDesc("location-1");

        // Verify results
        assertNotNull(locationAlerts);
        assertEquals(2, locationAlerts.size());
        // Newest alert should be first
        assertEquals("test-alert-2", locationAlerts.get(0).getAlertId());
        assertEquals("test-alert-1", locationAlerts.get(1).getAlertId());
    }

    @Test
    public void testFindByAlertTypeOrderByTimestampDesc() {
        // Create test data
        WeatherAlert alert1 = new WeatherAlert(
                "test-alert-1",
                "location-1",
                "Test Location",
                "HIGH_TEMPERATURE",
                "High temperature alert",
                35.5,
                "HIGH",
                LocalDateTime.now().minusHours(2)
        );

        WeatherAlert alert2 = new WeatherAlert(
                "test-alert-2",
                "location-2",
                "Another Location",
                "HIGH_TEMPERATURE",
                "High temperature alert",
                36.0,
                "HIGH",
                LocalDateTime.now().minusHours(1)
        );

        WeatherAlert alert3 = new WeatherAlert(
                "test-alert-3",
                "location-3",
                "Third Location",
                "HIGH_WIND",
                "High wind alert",
                25.0,
                "MEDIUM",
                LocalDateTime.now()
        );

        // Save test data
        alertRepository.saveAll(Arrays.asList(alert1, alert2, alert3));

        // Test the repository method
        List<WeatherAlert> typeAlerts = alertRepository.findByAlertTypeOrderByTimestampDesc("HIGH_TEMPERATURE");

        // Verify results
        assertNotNull(typeAlerts);
        assertEquals(2, typeAlerts.size());
        // Newest alert should be first
        assertEquals("test-alert-2", typeAlerts.get(0).getAlertId());
        assertEquals("test-alert-1", typeAlerts.get(1).getAlertId());
    }

    @Test
    public void testFindAllByOrderByTimestampDescWithPagination() {
        // Create and save 25 test alerts with different timestamps
        for (int i = 0; i < 25; i++) {
            WeatherAlert alert = new WeatherAlert(
                    "test-alert-" + i,
                    "location-" + (i % 5),
                    "Location " + (i % 5),
                    "TEST_ALERT",
                    "Test alert message " + i,
                    i * 1.5,
                    i % 2 == 0 ? "HIGH" : "MEDIUM",
                    LocalDateTime.now().minusMinutes(i)
            );
            alertRepository.save(alert);
        }

        // Test pagination - get first page (10 items)
        List<WeatherAlert> firstPage = alertRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, 10));
        
        // Verify results
        assertNotNull(firstPage);
        assertEquals(10, firstPage.size());
        
        // First item should be the newest (test-alert-0)
        assertEquals("test-alert-0", firstPage.get(0).getAlertId());
        
        // Test second page
        List<WeatherAlert> secondPage = alertRepository.findAllByOrderByTimestampDesc(PageRequest.of(1, 10));
        
        // Verify results
        assertNotNull(secondPage);
        assertEquals(10, secondPage.size());
        
        // First item of second page should be test-alert-10
        assertEquals("test-alert-10", secondPage.get(0).getAlertId());
    }
}
