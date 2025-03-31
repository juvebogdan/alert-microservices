package com.example.weatherproducer.controller;

import com.example.weatherproducer.model.WeatherData;
import com.example.weatherproducer.service.WeatherKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherKafkaProducer producer;

    @PostMapping
    public ResponseEntity<WeatherData> publishWeatherData(@RequestBody WeatherData weatherData) {
        // Set current timestamp if not provided
        if (weatherData.getTimestamp() == null) {
            weatherData.setTimestamp(LocalDateTime.now());
        }
        
        producer.sendWeatherData(weatherData);
        return new ResponseEntity<>(weatherData, HttpStatus.CREATED);
    }
}
