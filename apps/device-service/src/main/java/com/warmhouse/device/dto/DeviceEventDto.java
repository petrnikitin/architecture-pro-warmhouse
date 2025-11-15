package com.warmhouse.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEventDto {
    private String eventId;
    private String deviceId;
    private String deviceType;
    private String eventType; // CONNECTED, DISCONNECTED, STATE_CHANGED
    private Object previousState;
    private Object newState;
    private String changedBy;
    private Instant timestamp;
}
