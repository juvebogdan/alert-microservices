package com.example.weatheranalyzer.service;

import com.example.weatheranalyzer.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherDataConsumer {

    private final EnhancedWeatherAnalyzer weatherAnalyzer;
    
    @KafkaListener(topics = "weather-data", groupId = "weather-analyzer-group")
    public void consume(WeatherData weatherData) {
        log.info("Received weather data: {}", weatherData);
        
        // Use the enhanced analyzer to process the weather data
        weatherAnalyzer.analyzeWeatherData(weatherData);
    }
}
