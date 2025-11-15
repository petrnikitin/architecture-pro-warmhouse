package com.warmhouse.device.controller;

import com.warmhouse.device.dto.DeviceCommandDto;
import com.warmhouse.device.dto.DeviceDto;
import com.warmhouse.device.dto.DeviceEventDto;
import com.warmhouse.device.kafka.DeviceEventProducer;
import com.warmhouse.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceEventProducer deviceEventProducer;

    /**
     * Получить все устройства
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDevices(
            @RequestParam(required = false) String houseId,
            @RequestParam(required = false) String status) {
        
        log.info("GET /api/v1/devices?houseId={}&status={}", houseId, status);
        
        List<DeviceDto> devices;
        
        if (houseId != null) {
            devices = deviceService.getDevicesByHouseId(houseId);
        } else {
            devices = deviceService.getAllDevices();
        }
        
        // Фильтрация по статусу если указан
        if (status != null) {
            devices = devices.stream()
                    .filter(d -> status.equals(d.getStatus()))
                    .toList();
        }
        
        return ResponseEntity.ok(Map.of(
                "content", devices,
                "totalElements", devices.size()
        ));
    }

    /**
     * Получить устройство по ID
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable String deviceId) {
        log.info("GET /api/v1/devices/{}", deviceId);
        
        return deviceService.getDeviceById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Зарегистрировать новое устройство
     */
    @PostMapping
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto device) {
        log.info("POST /api/v1/devices: {}", device);
        
        DeviceDto created = deviceService.createDevice(device);
        
        // Отправляем событие в Kafka
        DeviceEventDto event = DeviceEventDto.builder()
                .eventId(UUID.randomUUID().toString())
                .deviceId(created.getId())
                .deviceType("SENSOR")
                .eventType("DEVICE_REGISTERED")
                .newState(created)
                .timestamp(Instant.now())
                .build();
        
        deviceEventProducer.sendDeviceEvent(event);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Обновить устройство
     */
    @PutMapping("/{deviceId}")
    public ResponseEntity<DeviceDto> updateDevice(
            @PathVariable String deviceId,
            @RequestBody DeviceDto updates) {
        
        log.info("PUT /api/v1/devices/{}: {}", deviceId, updates);
        
        return deviceService.updateDevice(deviceId, updates)
                .map(updated -> {
                    // Отправляем событие в Kafka
                    DeviceEventDto event = DeviceEventDto.builder()
                            .eventId(UUID.randomUUID().toString())
                            .deviceId(deviceId)
                            .eventType("DEVICE_UPDATED")
                            .newState(updated)
                            .timestamp(Instant.now())
                            .build();
                    
                    deviceEventProducer.sendDeviceEvent(event);
                    
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удалить устройство
     */
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String deviceId) {
        log.info("DELETE /api/v1/devices/{}", deviceId);
        
        if (deviceService.deleteDevice(deviceId)) {
            // Отправляем событие в Kafka
            DeviceEventDto event = DeviceEventDto.builder()
                    .eventId(UUID.randomUUID().toString())
                    .deviceId(deviceId)
                    .eventType("DEVICE_DELETED")
                    .timestamp(Instant.now())
                    .build();
            
            deviceEventProducer.sendDeviceEvent(event);
            
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Отправить команду устройству
     */
    @PostMapping("/{deviceId}/command")
    public ResponseEntity<?> sendCommand(
            @PathVariable String deviceId,
            @RequestBody DeviceCommandDto command) {
        
        log.info("POST /api/v1/devices/{}/command: {}", deviceId, command);
        
        return deviceService.getDeviceById(deviceId)
                .map(device -> {
                    if ("OFFLINE".equals(device.getStatus())) {
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of(
                                        "error", "Device is offline and cannot receive commands",
                                        "deviceId", deviceId
                                ));
                    }
                    
                    String commandId = UUID.randomUUID().toString();
                    
                    // Отправляем событие команды в Kafka
                    DeviceEventDto event = DeviceEventDto.builder()
                            .eventId(commandId)
                            .deviceId(deviceId)
                            .eventType("COMMAND_SENT")
                            .newState(command)
                            .timestamp(Instant.now())
                            .build();
                    
                    deviceEventProducer.sendDeviceEvent(event);
                    
                    return ResponseEntity.ok(Map.of(
                            "commandId", commandId,
                            "status", "ACCEPTED",
                            "message", "Command is being executed",
                            "timestamp", Instant.now().toString()
                    ));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "device-service"
        ));
    }
}
