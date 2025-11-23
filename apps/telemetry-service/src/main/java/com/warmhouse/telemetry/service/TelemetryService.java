package com.warmhouse.telemetry.service;

import com.warmhouse.telemetry.dto.TelemetryDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TelemetryService {

    // In-memory storage for telemetry data (в реальном приложении использовалась бы БД)
    private final Map<String, List<TelemetryDataDto>> telemetryStore = new ConcurrentHashMap<>();

    /**
     * Сохраняет телеметрические данные
     */
    public TelemetryDataDto saveTelemetry(TelemetryDataDto telemetry) {
        if (telemetry.getId() == null) {
            telemetry.setId(UUID.randomUUID().toString());
        }
        if (telemetry.getTimestamp() == null) {
            telemetry.setTimestamp(Instant.now());
        }

        telemetryStore.computeIfAbsent(telemetry.getDeviceId(), k -> new ArrayList<>())
                .add(telemetry);

        log.info("Telemetry saved: deviceId={}, sensorType={}, value={}", 
                telemetry.getDeviceId(), telemetry.getSensorType(), telemetry.getValue());
        
        return telemetry;
    }

    /**
     * Получает телеметрию для устройства
     */
    public List<TelemetryDataDto> getTelemetryByDeviceId(String deviceId, Integer limit) {
        List<TelemetryDataDto> data = telemetryStore.getOrDefault(deviceId, new ArrayList<>());
        
        if (limit != null && limit > 0 && data.size() > limit) {
            // Возвращаем последние N записей
            return data.subList(Math.max(0, data.size() - limit), data.size());
        }
        
        return new ArrayList<>(data);
    }

    /**
     * Получает последнее значение телеметрии для устройства
     */
    public Optional<TelemetryDataDto> getLatestTelemetry(String deviceId) {
        List<TelemetryDataDto> data = telemetryStore.get(deviceId);
        if (data == null || data.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(data.get(data.size() - 1));
    }

    /**
     * Получает все данные телеметрии
     */
    public List<TelemetryDataDto> getAllTelemetry() {
        List<TelemetryDataDto> all = new ArrayList<>();
        telemetryStore.values().forEach(all::addAll);
        return all;
    }

    /**
     * Удаляет старые данные (для управления памятью)
     */
    public void cleanOldData(int keepLastN) {
        telemetryStore.forEach((deviceId, dataList) -> {
            if (dataList.size() > keepLastN) {
                List<TelemetryDataDto> toKeep = dataList.subList(
                        dataList.size() - keepLastN, 
                        dataList.size()
                );
                telemetryStore.put(deviceId, new ArrayList<>(toKeep));
            }
        });
        log.info("Old telemetry data cleaned, keeping last {} records per device", keepLastN);
    }
}
