package com.example.alertnotification.repository;

import com.example.alertnotification.model.WeatherAlert;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, String> {
    
    // Find alerts by location ID, ordered by timestamp (newest first)
    List<WeatherAlert> findByLocationIdOrderByTimestampDesc(String locationId);
    
    // Find alerts by alert type, ordered by timestamp (newest first)
    List<WeatherAlert> findByAlertTypeOrderByTimestampDesc(String alertType);
    
    // Find alerts by severity, ordered by timestamp (newest first)
    List<WeatherAlert> findBySeverityOrderByTimestampDesc(String severity);
    
    // Find all alerts ordered by timestamp (newest first)
    List<WeatherAlert> findAllByOrderByTimestampDesc();
    
    // Find all alerts with pagination
    List<WeatherAlert> findAllByOrderByTimestampDesc(Pageable pageable);
}
