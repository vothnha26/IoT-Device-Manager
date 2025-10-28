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
    
    public DeviceSessionRegistry getRegistry() {
        return registry;
    }

    public boolean sendCommandToDevice(Long deviceId, String command) {
        if (deviceId == null || command == null) {
            log.warn("⚠️  sendCommandToDevice: Invalid params - deviceId={}, command={}", deviceId, command);
            return false;
        }
        
        // Lấy session từ registry
        WebSocketSession session = registry.getSession(String.valueOf(deviceId));
        
        if (session == null) {
            log.warn("❌ Device {} - WebSocket session NOT FOUND in registry", deviceId);
            log.info("💡 Hint: Check if ESP32 device {} is connected to /ws/device?deviceId={}", deviceId, deviceId);
            return false;
        }
        
        if (!session.isOpen()) {
            log.warn("❌ Device {} - WebSocket session is CLOSED", deviceId);
            return false;
        }
        
        try {
            // Gửi lệnh xuống ESP32
            session.sendMessage(new TextMessage(command));
            log.info("✅ Successfully sent command to device {}: '{}'", deviceId, command);
            log.info("   → Session ID: {}", session.getId());
            log.info("   → Remote Address: {}", session.getRemoteAddress());
            return true;
        } catch (Exception e) {
            log.error("❌ Failed sending command to device {}: {}", deviceId, e.getMessage(), e);
            return false;
        }
    }
}
