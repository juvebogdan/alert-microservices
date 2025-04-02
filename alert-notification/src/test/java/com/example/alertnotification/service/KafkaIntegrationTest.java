package com.example.alertnotification.service;  // Match this with the directory where the file is located

import com.example.alertnotification.model.WeatherAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KafkaIntegrationTest {

    private static final String TOPIC_NAME = "test-weather-alerts";

    // No @Container annotation, we'll manage it manually
    private static final KafkaContainer kafkaContainer = 
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.0"));

    private KafkaProducer<String, WeatherAlert> producer;
    private KafkaConsumer<String, WeatherAlert> consumer;
    private ObjectMapper objectMapper;

    @BeforeAll
    void startContainer() {
        kafkaContainer.start();
    }

    @AfterAll
    void stopContainer() {
        kafkaContainer.stop();
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Set up the producer
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        producer = new KafkaProducer<>(producerProps);
        
        // Set up the consumer
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.*");
        
        consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
    }

    @Test
    void testKafkaProducerConsumer() {
        // Create a test alert
        WeatherAlert testAlert = new WeatherAlert(
                UUID.randomUUID().toString(),
                "test-location-1",
                "Test Location",
                "HIGH_TEMPERATURE",
                "Test temperature alert",
                38.5,
                "HIGH",
                LocalDateTime.now()
        );

        // Send the alert to Kafka
        producer.send(new ProducerRecord<>(TOPIC_NAME, testAlert.getAlertId(), testAlert));
        producer.flush();
        
        System.out.println("Message sent to Kafka, waiting to consume...");
        
        // Consume the message from Kafka
        ConsumerRecords<String, WeatherAlert> records = consumer.poll(Duration.ofSeconds(10));
        
        // Verify we received the message
        assertNotNull(records);
        assertEquals(1, records.count(), "Expected to receive exactly one record");
        
        ConsumerRecord<String, WeatherAlert> record = records.iterator().next();
        assertNotNull(record);
        assertNotNull(record.value());
        
        WeatherAlert receivedAlert = record.value();
        assertEquals(testAlert.getAlertId(), receivedAlert.getAlertId());
        assertEquals(testAlert.getLocationId(), receivedAlert.getLocationId());
        assertEquals(testAlert.getAlertType(), receivedAlert.getAlertType());
        assertEquals(testAlert.getSeverity(), receivedAlert.getSeverity());
    }
}
