package com.warmhouse.device.service;

import com.warmhouse.device.dto.DeviceDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DeviceService {

    // In-memory storage for devices (в реальном приложении использовалась бы БД)
    private final Map<String, DeviceDto> deviceStore = new ConcurrentHashMap<>();

    /**
     * Создает новое устройство
     */
    public DeviceDto createDevice(DeviceDto device) {
        if (device.getId() == null) {
            device.setId(UUID.randomUUID().toString());
        }
        
        Instant now = Instant.now();
        device.setCreatedAt(now);
        device.setUpdatedAt(now);
        
        if (device.getStatus() == null) {
            device.setStatus("OFFLINE");
        }
        if (device.getConnectionStatus() == null) {
            device.setConnectionStatus("DISCONNECTED");
        }
        if (device.getBatteryLevel() == null) {
            device.setBatteryLevel(100);
        }

        deviceStore.put(device.getId(), device);
        
        log.info("Device created: id={}, name={}, serialNumber={}", 
                device.getId(), device.getName(), device.getSerialNumber());
        
        return device;
    }

    /**
     * Получает устройство по ID
     */
    public Optional<DeviceDto> getDeviceById(String deviceId) {
        return Optional.ofNullable(deviceStore.get(deviceId));
    }

    /**
     * Получает все устройства
     */
    public List<DeviceDto> getAllDevices() {
        return new ArrayList<>(deviceStore.values());
    }

    /**
     * Получает устройства по houseId
     */
    public List<DeviceDto> getDevicesByHouseId(String houseId) {
        return deviceStore.values().stream()
                .filter(d -> houseId.equals(d.getHouseId()))
                .toList();
    }

    /**
     * Обновляет устройство
     */
    public Optional<DeviceDto> updateDevice(String deviceId, DeviceDto updates) {
        DeviceDto existing = deviceStore.get(deviceId);
        if (existing == null) {
            return Optional.empty();
        }

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getRoomId() != null) {
            existing.setRoomId(updates.getRoomId());
        }
        if (updates.getStatus() != null) {
            existing.setStatus(updates.getStatus());
        }
        if (updates.getFirmwareVersion() != null) {
            existing.setFirmwareVersion(updates.getFirmwareVersion());
        }
        
        existing.setUpdatedAt(Instant.now());
        
        log.info("Device updated: id={}, name={}", deviceId, existing.getName());
        
        return Optional.of(existing);
    }

    /**
     * Удаляет устройство
     */
    public boolean deleteDevice(String deviceId) {
        DeviceDto removed = deviceStore.remove(deviceId);
        if (removed != null) {
            log.info("Device deleted: id={}, name={}", deviceId, removed.getName());
            return true;
        }
        return false;
    }

    /**
     * Обновляет статус устройства
     */
    public Optional<DeviceDto> updateDeviceStatus(String deviceId, String status, String connectionStatus) {
        DeviceDto device = deviceStore.get(deviceId);
        if (device == null) {
            return Optional.empty();
        }

        device.setStatus(status);
        device.setConnectionStatus(connectionStatus);
        device.setLastSeen(Instant.now());
        device.setUpdatedAt(Instant.now());
        
        log.info("Device status updated: id={}, status={}, connectionStatus={}", 
                deviceId, status, connectionStatus);
        
        return Optional.of(device);
    }
}
