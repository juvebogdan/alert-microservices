package com.example.weatheranalyzer.service;

import com.example.weatheranalyzer.model.WeatherAlert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherAlertProducer {

    private static final String TOPIC = "weather-alerts";
    private final KafkaTemplate<String, WeatherAlert> alertKafkaTemplate;

    public void sendAlert(WeatherAlert alert) {
        log.info("Sending weather alert to Kafka: {}", alert);
        alertKafkaTemplate.send(TOPIC, alert.getLocationId(), alert);
    }
}
