package com.iot.management.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class DeviceMessagingService {
    private static final Logger log = LoggerFactory.getLogger(DeviceMessagingService.class);

    private final DeviceSessionRegistry registry;

    public DeviceMessagingService(DeviceSessionRegistry registry) {
        this.registry = registry;
    }

    public boolean sendCommandToDevice(Long deviceId, String command) {
        if (deviceId == null || command == null) return false;
        WebSocketSession session = registry.getSession(String.valueOf(deviceId));
        if (session == null || !session.isOpen()) {
            log.warn("Device {} not connected via WebSocket", deviceId);
            return false;
        }
        try {
            session.sendMessage(new TextMessage(command));
            return true;
        } catch (Exception e) {
            log.error("Failed sending command to device {}: {}", deviceId, e.getMessage());
            return false;
        }
    }
}
