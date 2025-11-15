package com.warmhouse.temperature.service;

import com.warmhouse.temperature.dto.TemperatureResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Slf4j
@Service
public class TemperatureService {

    private final Random random = new Random();
    private static final String[] CONDITIONS = {"Clear", "Cloudy", "Rainy", "Snowy", "Foggy", "Windy"};
    private static final String[] STATUSES = {"active", "normal", "ok", "operational"};

    /**
     * Генерирует температуру по названию локации
     */
    public TemperatureResponse generateTemperature(String location, String sensorId) {
        double temperature = generateRandomTemperature();
        String condition = CONDITIONS[random.nextInt(CONDITIONS.length)];

        // Если sensorId не указан, определяем его по location
        if (sensorId == null || sensorId.isEmpty()) {
            sensorId = locationToSensorId(location);
        }

        // Если location не указан, определяем его по sensorId
        if (location == null || location.isEmpty()) {
            location = sensorIdToLocation(sensorId);
        }

        log.info("Generated temperature {} for location '{}' (sensor_id: {})", temperature, location, sensorId);

        return buildResponse(temperature, location, sensorId, condition);
    }

    /**
     * Генерирует температуру по ID датчика
     */
    public TemperatureResponse generateTemperatureById(String sensorId) {
        String location = sensorIdToLocation(sensorId);
        return generateTemperature(location, sensorId);
    }

    /**
     * Генерирует случайную температуру в диапазоне от -30 до +40 градусов
     */
    private double generateRandomTemperature() {
        return Math.round((random.nextDouble() * 70 - 30) * 10.0) / 10.0;
    }

    /**
     * Маппинг location -> sensorId (по заданию)
     */
    private String locationToSensorId(String location) {
        if (location == null) return "0";

        return switch (location) {
            case "Living Room" -> "1";
            case "Bedroom" -> "2";
            case "Kitchen" -> "3";
            default -> "0";
        };
    }

    /**
     * Маппинг sensorId -> location (по заданию)
     */
    private String sensorIdToLocation(String sensorId) {
        if (sensorId == null) return "Unknown";

        return switch (sensorId) {
            case "1" -> "Living Room";
            case "2" -> "Bedroom";
            case "3" -> "Kitchen";
            default -> "Unknown";
        };
    }

    /**
     * Строит объект ответа с температурными данными
     */
    private TemperatureResponse buildResponse(double temperature, String location,
                                               String sensorId, String condition) {
        int humidity = random.nextInt(71) + 20;
        int windSpeed = random.nextInt(21);
        String status = STATUSES[random.nextInt(STATUSES.length)];

        return TemperatureResponse.builder()
                .value(temperature)
                .unit("celsius")
                .timestamp(Instant.now())
                .location(location)
                .status(status)
                .sensorId(sensorId)
                .sensorType("temperature")
                .description(String.format("Temperature sensor in %s", location))
                .condition(condition)
                .humidity(humidity)
                .windSpeed(windSpeed)
                .build();
    }
}
