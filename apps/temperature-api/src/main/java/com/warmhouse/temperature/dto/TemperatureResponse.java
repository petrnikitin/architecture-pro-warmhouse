package com.warmhouse.temperature.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureResponse {
    // Основные поля для Go-монолита
    private Double value;
    private String unit;
    private Instant timestamp;
    private String location;
    private String status;

    @JsonProperty("sensor_id")
    private String sensorId;

    @JsonProperty("sensor_type")
    private String sensorType;

    private String description;

    // Дополнительные поля (для расширенной информации)
    private String condition;
    private Integer humidity;
    private Integer windSpeed;
}
