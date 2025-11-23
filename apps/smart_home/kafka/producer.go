package kafka

import (
	"context"
	"encoding/json"
	"log"
	"time"

	"github.com/segmentio/kafka-go"
)

type TelemetryEvent struct {
	EventID    string    `json:"eventId"`
	DeviceID   string    `json:"deviceId"`
	SensorType string    `json:"sensorType"`
	Value      float64   `json:"value"`
	Unit       string    `json:"unit"`
	Quality    string    `json:"quality"`
	RoomID     string    `json:"roomId,omitempty"`
	HouseID    string    `json:"houseId,omitempty"`
	Timestamp  time.Time `json:"timestamp"`
}

type Producer struct {
	writer *kafka.Writer
}

func NewProducer(brokers []string) *Producer {
	return &Producer{
		writer: &kafka.Writer{
			Addr:     kafka.TCP(brokers...),
			Balancer: &kafka.LeastBytes{},
		},
	}
}

func (p *Producer) SendTelemetryEvent(ctx context.Context, event TelemetryEvent) error {
	eventJSON, err := json.Marshal(event)
	if err != nil {
		log.Printf("Error marshalling telemetry event: %v", err)
		return err
	}

	err = p.writer.WriteMessages(ctx, kafka.Message{
		Topic: "telemetry.data",
		Key:   []byte(event.DeviceID),
		Value: eventJSON,
	})

	if err != nil {
		log.Printf("Error sending telemetry event to Kafka: %v", err)
		return err
	}

	log.Printf("Telemetry event sent to Kafka: deviceId=%s, sensorType=%s, value=%f", 
		event.DeviceID, event.SensorType, event.Value)
	
	return nil
}

func (p *Producer) Close() error {
	if p.writer != nil {
		return p.writer.Close()
	}
	return nil
}
