package com.example.weatherproducer.service;

import com.example.weatherproducer.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherKafkaProducer {

    private static final String TOPIC = "weather-data";
    private final KafkaTemplate<String, WeatherData> kafkaTemplate;

    public void sendWeatherData(WeatherData weatherData) {
        log.info("Sending weather data to Kafka: {}", weatherData);
        kafkaTemplate.send(TOPIC, weatherData.getLocationId(), weatherData);
    }
}
