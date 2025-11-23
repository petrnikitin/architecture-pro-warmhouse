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
public class TelemetryDataDto {
    private String id;
    private String deviceId;
    private String sensorType;
    private Double value;
    private String unit;
    private String quality;
    private Instant timestamp;
}
