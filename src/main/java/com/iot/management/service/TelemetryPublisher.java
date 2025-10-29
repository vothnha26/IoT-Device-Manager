package com.iot.management.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.iot.management.model.dto.device.TelemetryMessage;

@Service
public class TelemetryPublisher {

    private final SimpMessagingTemplate messaging;

    public TelemetryPublisher(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void publish(TelemetryMessage msg) {
        if (msg.getRoomId() != null) {
            messaging.convertAndSend("/topic/room/" + msg.getRoomId(), msg);
        }
        if (msg.getDeviceId() != null) {
            messaging.convertAndSend("/topic/device/" + msg.getDeviceId(), msg);
        }
    }
}
