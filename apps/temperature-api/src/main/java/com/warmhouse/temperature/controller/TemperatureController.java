package com.warmhouse.temperature.controller;

import com.warmhouse.temperature.dto.TemperatureResponse;
import com.warmhouse.temperature.service.TemperatureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class TemperatureController {

    private final TemperatureService temperatureService;

    /**
     * GET /temperature?location=<location>
     * Получение температуры по названию локации
     */
    @GetMapping("/temperature")
    public ResponseEntity<TemperatureResponse> getTemperature(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sensorId) {

        log.info("Received temperature request for location: '{}', sensorId: '{}'", location, sensorId);

        // Если не указаны ни location ни sensorId - ошибка
        if ((location == null || location.trim().isEmpty()) &&
            (sensorId == null || sensorId.trim().isEmpty())) {
            log.warn("Both location and sensorId are missing");
            return ResponseEntity.badRequest().build();
        }

        TemperatureResponse response = temperatureService.generateTemperature(location, sensorId);
        log.info("Returning temperature {} for location: '{}' (sensor_id: {})",
                 response.getValue(), response.getLocation(), response.getSensorId());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /temperature/:id
     * Получение температуры по ID датчика (для совместимости с Go-монолитом)
     */
    @GetMapping("/temperature/{sensorId}")
    public ResponseEntity<TemperatureResponse> getTemperatureById(
            @PathVariable String sensorId) {

        log.info("Received temperature request for sensor ID: '{}'", sensorId);

        if (sensorId == null || sensorId.trim().isEmpty()) {
            log.warn("Sensor ID is missing");
            return ResponseEntity.badRequest().build();
        }

        TemperatureResponse response = temperatureService.generateTemperatureById(sensorId);
        log.info("Returning temperature {} for sensor_id: {}",
                 response.getValue(), response.getSensorId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
