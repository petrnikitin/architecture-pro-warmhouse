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
public class DeviceDto {
    private String id;
    private String deviceTypeId;
    private String houseId;
    private String roomId;
    private String serialNumber;
    private String name;
    private String macAddress;
    private String ipAddress;
    private String firmwareVersion;
    private String status; // ONLINE, OFFLINE, ERROR, MAINTENANCE
    private String connectionStatus; // CONNECTED, DISCONNECTED, PAIRING
    private Instant lastSeen;
    private Integer batteryLevel;
    private Instant createdAt;
    private Instant updatedAt;
}
