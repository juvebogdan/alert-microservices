package com.example.alertnotification.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    // Create a mock KafkaTemplate that we can use in tests
    @Bean
    @Primary
    public KafkaTemplate<?, ?> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
