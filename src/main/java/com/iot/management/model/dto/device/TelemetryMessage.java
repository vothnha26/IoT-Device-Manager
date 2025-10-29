package com.iot.management.model.dto.device;

import java.time.Instant;

public class TelemetryMessage {

    private Long deviceId;
    private Long roomId;
    private Instant timestamp;
    private String type;
    private Double value;
    private String state;

    public TelemetryMessage() {
    }

    public TelemetryMessage(Long deviceId, Long roomId, Instant timestamp, String type, Double value, String state) {
        this.deviceId = deviceId;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.type = type;
        this.value = value;
        this.state = state;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
