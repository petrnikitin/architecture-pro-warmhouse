package com.warmhouse.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCommandDto {
    private String action; // TURN_ON, TURN_OFF, SET_VALUE, RESET, RESTART
    private Map<String, Object> parameters;
}
