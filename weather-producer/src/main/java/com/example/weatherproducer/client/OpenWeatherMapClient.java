package com.example.weatherproducer.client;

import com.example.weatherproducer.model.WeatherData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenWeatherMapClient {

    private final WebClient webClient;
    
    @Value("${openweathermap.api.key}")
    private String apiKey;
    
    // Map to store location IDs for consistency
    private final Map<String, String> locationIdMap = new HashMap<>();
    
    // Pre-defined list of cities for demo purposes
    private final String[] cities = {
        "London,uk", "New York,us", "Tokyo,jp", "Sydney,au", 
        "Paris,fr", "Berlin,de", "Cairo,eg", "Mumbai,in"
    };
    
    /**
     * Fetch weather data for a specific city
     */
    public Mono<WeatherData> getWeatherForCity(String city) {
        log.info("Fetching weather data for {}", city);
        
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/data/2.5/weather")
                .queryParam("q", city)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .build())
            .retrieve()
            .bodyToMono(OpenWeatherMapResponse.class)
            .map(this::convertToWeatherData)
            .doOnError(e -> log.error("Error fetching weather data: {}", e.getMessage()));
    }
    
    /**
     * Get a random city from our pre-defined list
     */
    public String getRandomCity() {
        int index = (int) (Math.random() * cities.length);
        return cities[index];
    }
    
    /**
     * Convert API response to our internal WeatherData model
     */
    private WeatherData convertToWeatherData(OpenWeatherMapResponse response) {
        // Generate consistent locationId for the same city
        String locationId = locationIdMap.computeIfAbsent(
            response.getName() + "," + response.getSys().getCountry(),
            k -> UUID.randomUUID().toString()
        );
        
        return new WeatherData(
            locationId,
            response.getName() + ", " + response.getSys().getCountry(),
            response.getMain().getTemp(),
            response.getMain().getHumidity(),
            response.getWind().getSpeed(),
            getWindDirection(response.getWind().getDeg()),
            response.getRain() != null ? response.getRain().getOneHour() : 0.0,
            LocalDateTime.now()
        );
    }
    
    /**
     * Convert wind degrees to cardinal direction
     */
    private String getWindDirection(double degrees) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return directions[(int) Math.round(degrees % 360 / 45) % 8];
    }
    
    // Inner classes to map OpenWeatherMap JSON response
    
    public static class OpenWeatherMapResponse {
        private MainData main;
        private WindData wind;
        private RainData rain;
        private String name;
        private SysData sys;
        
        public MainData getMain() { return main; }
        public void setMain(MainData main) { this.main = main; }
        
        public WindData getWind() { return wind; }
        public void setWind(WindData wind) { this.wind = wind; }
        
        public RainData getRain() { return rain; }
        public void setRain(RainData rain) { this.rain = rain; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public SysData getSys() { return sys; }
        public void setSys(SysData sys) { this.sys = sys; }
    }
    
    public static class MainData {
        private double temp;
        private double humidity;
        
        public double getTemp() { return temp; }
        public void setTemp(double temp) { this.temp = temp; }
        
        public double getHumidity() { return humidity; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
    }
    
    public static class WindData {
        private double speed;
        private double deg;
        
        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
        
        public double getDeg() { return deg; }
        public void setDeg(double deg) { this.deg = deg; }
    }
    
    public static class RainData {
        private double oneHour;
        
        // OpenWeatherMap uses "1h" as the key, Jackson will map it to this setter
        public void set1h(double oneHour) { this.oneHour = oneHour; }
        public double getOneHour() { return oneHour; }
    }
    
    public static class SysData {
        private String country;
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
    }
}
