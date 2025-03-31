package com.example.weatherproducer.service;

import com.example.weatherproducer.client.OpenWeatherMapClient;
import com.example.weatherproducer.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class WeatherDataScheduler {

    private final OpenWeatherMapClient weatherClient;
    private final WeatherKafkaProducer kafkaProducer;
    
    /**
     * Fetch weather data every 5 minutes
     * We use a randomized approach to cycle through different cities
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void fetchWeatherData() {
        String city = weatherClient.getRandomCity();
        
        weatherClient.getWeatherForCity(city)
            .subscribe(
                // On success, send to Kafka
                this::processWeatherData,
                // On error, log it
                error -> log.error("Error fetching weather data: {}", error.getMessage())
            );
    }
    
    /**
     * For demonstration purposes, also fetch on application startup
     */
    @Scheduled(initialDelay = 10000, fixedDelay = Long.MAX_VALUE)
    public void fetchInitialWeatherData() {
        // Fetch data for all predefined cities on startup
        for (int i = 0; i < 3; i++) { // Limit to 3 cities for the initial load
            String city = weatherClient.getRandomCity();
            
            weatherClient.getWeatherForCity(city)
                .subscribe(
                    this::processWeatherData,
                    error -> log.error("Error fetching initial weather data: {}", error.getMessage())
                );
        }
    }
    
    private void processWeatherData(WeatherData weatherData) {
        log.info("Received weather data from API: {}", weatherData);
        kafkaProducer.sendWeatherData(weatherData);
    }
}
