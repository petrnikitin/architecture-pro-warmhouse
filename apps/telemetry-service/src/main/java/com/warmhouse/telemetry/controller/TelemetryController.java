package com.warmhouse.telemetry.controller;

import com.warmhouse.telemetry.dto.TelemetryDataDto;
import com.warmhouse.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetry")
@RequiredArgsConstructor
@Slf4j
public class TelemetryController {

    private final TelemetryService telemetryService;

    /**
     * Получить телеметрические данные по устройству
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTelemetry(
            @RequestParam(required = true) String deviceId,
            @RequestParam(required = false) Integer limit) {
        
        log.info("GET /api/v1/telemetry?deviceId={}&limit={}", deviceId, limit);
        
        List<TelemetryDataDto> data = telemetryService.getTelemetryByDeviceId(deviceId, limit);
        
        return ResponseEntity.ok(Map.of(
                "data", data,
                "count", data.size(),
                "deviceId", deviceId
        ));
    }

    /**
     * Получить последние показания для устройства
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestTelemetry(@RequestParam(required = true) String deviceId) {
        log.info("GET /api/v1/telemetry/latest?deviceId={}", deviceId);
        
        return telemetryService.getLatestTelemetry(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Записать телеметрические данные (REST endpoint для прямой отправки)
     */
    @PostMapping
    public ResponseEntity<TelemetryDataDto> createTelemetry(@RequestBody TelemetryDataDto telemetry) {
        log.info("POST /api/v1/telemetry: {}", telemetry);
        
        TelemetryDataDto saved = telemetryService.saveTelemetry(telemetry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Получить всю телеметрию (для отладки)
     */
    @GetMapping("/all")
    public ResponseEntity<List<TelemetryDataDto>> getAllTelemetry() {
        log.info("GET /api/v1/telemetry/all");
        return ResponseEntity.ok(telemetryService.getAllTelemetry());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "telemetry-service"
        ));
    }
}
