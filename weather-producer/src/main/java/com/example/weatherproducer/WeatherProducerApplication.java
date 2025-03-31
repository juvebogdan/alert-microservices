package com.example.weatherproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@SpringBootApplication
@EnableScheduling
public class WeatherProducerApplication {

    public static void main(String[] args) {
        try {
            // Load API key from .env file
            Path dotenvPath = Path.of(".env");
            if (Files.exists(dotenvPath)) {
                Properties props = new Properties();
                props.load(Files.newBufferedReader(dotenvPath));
                
                String apiKey = props.getProperty("OPENWEATHERMAP_API_KEY");
                if (apiKey != null) {
                    System.setProperty("OPENWEATHERMAP_API_KEY", apiKey);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
        
        SpringApplication.run(WeatherProducerApplication.class, args);
    }
}
