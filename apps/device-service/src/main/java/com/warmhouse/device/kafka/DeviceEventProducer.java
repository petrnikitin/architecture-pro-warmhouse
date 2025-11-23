package com.warmhouse.device.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.warmhouse.device.dto.DeviceEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String DEVICE_EVENTS_TOPIC = "device.events";

    public void sendDeviceEvent(DeviceEventDto event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(DEVICE_EVENTS_TOPIC, event.getDeviceId(), message);
            log.info("Device event sent: eventType={}, deviceId={}", event.getEventType(), event.getDeviceId());
        } catch (Exception e) {
            log.error("Error sending device event: {}", e.getMessage(), e);
        }
    }
}
