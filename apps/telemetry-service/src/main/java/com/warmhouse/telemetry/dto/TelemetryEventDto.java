package com.warmhouse.telemetry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryEventDto {
    private String eventId;
    private String deviceId;
    private String sensorType;
    private Double value;
    private String unit;
    private String quality;
    private String roomId;
    private String houseId;
    private Instant timestamp;
}
