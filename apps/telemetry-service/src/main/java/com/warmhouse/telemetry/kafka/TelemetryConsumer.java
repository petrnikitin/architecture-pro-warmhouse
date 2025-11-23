package com.warmhouse.telemetry.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warmhouse.telemetry.dto.TelemetryDataDto;
import com.warmhouse.telemetry.dto.TelemetryEventDto;
import com.warmhouse.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final TelemetryService telemetryService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @KafkaListener(topics = "telemetry.data", groupId = "telemetry-service")
    public void consumeTelemetryData(String message) {
        try {
            log.info("Received telemetry message: {}", message);
            
            TelemetryEventDto event = objectMapper.readValue(message, TelemetryEventDto.class);
            
            // Конвертируем событие в DTO для сохранения
            TelemetryDataDto telemetryData = TelemetryDataDto.builder()
                    .id(event.getEventId())
                    .deviceId(event.getDeviceId())
                    .sensorType(event.getSensorType())
                    .value(event.getValue())
                    .unit(event.getUnit())
                    .quality(event.getQuality())
                    .timestamp(event.getTimestamp())
                    .build();
            
            telemetryService.saveTelemetry(telemetryData);
            
            log.info("Telemetry data processed successfully: deviceId={}, sensorType={}", 
                    event.getDeviceId(), event.getSensorType());
            
        } catch (Exception e) {
            log.error("Error processing telemetry message: {}", e.getMessage(), e);
        }
    }
}
