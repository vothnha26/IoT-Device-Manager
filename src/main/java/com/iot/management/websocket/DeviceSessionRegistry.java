package com.iot.management.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class DeviceSessionRegistry {
    private static final Logger log = LoggerFactory.getLogger(DeviceSessionRegistry.class);
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    public void register(String deviceId, WebSocketSession session) {
        if (deviceId == null || deviceId.isBlank() || session == null) return;
        sessions.put(deviceId, session);
        log.info("✅ Device {} registered (total: {})", deviceId, sessions.size());
    }

    // Convenience overloads for Long IDs
    public void register(Long deviceId, WebSocketSession session) {
        if (deviceId == null) return;
        register(String.valueOf(deviceId), session);
    }

    public void unregister(String deviceId, WebSocketSession session) {
        if (deviceId == null) return;
        WebSocketSession current = sessions.get(deviceId);
        if (current == session) {
            sessions.remove(deviceId, current);
            log.info("❌ Device {} unregistered (total: {})", deviceId, sessions.size());
        }
    }

    public void unregister(Long deviceId, WebSocketSession session) {
        if (deviceId == null) return;
        unregister(String.valueOf(deviceId), session);
    }

    public WebSocketSession getSession(String deviceId) {
        return sessions.get(deviceId);
    }

    public WebSocketSession getSession(Long deviceId) {
        return deviceId == null ? null : getSession(String.valueOf(deviceId));
    }

    public boolean isOnline(String deviceId) {
        WebSocketSession s = sessions.get(deviceId);
        return s != null && s.isOpen();
    }

    public boolean isOnline(Long deviceId) {
        return deviceId != null && isOnline(String.valueOf(deviceId));
    }
    
    /**
     * Gửi lệnh điều khiển đến thiết bị qua WebSocket
     * @param deviceId ID thiết bị
     * @param command Lệnh: "hoat_dong" hoặc "tat"
     * @return true nếu gửi thành công
     */
    public boolean sendCommand(Long deviceId, String command) {
        if (deviceId == null || command == null) {
            log.warn("⚠️  Invalid params: deviceId={}, command={}", deviceId, command);
            return false;
        }
        
        WebSocketSession session = getSession(deviceId);
        if (session == null || !session.isOpen()) {
            log.warn("⚠️  Device {} is offline, cannot send command", deviceId);
            return false;
        }
        
        try {
            // Gửi lệnh xuống ESP32
            session.sendMessage(new TextMessage(command));
            log.info("📤 Sent command to device {}: {}", deviceId, command);
            
            // Broadcast state update lên UI qua STOMP
            if (messagingTemplate != null) {
                String stateJson = "{\"deviceId\":\"" + deviceId + "\",\"state\":\"" + command + "\"}";
                messagingTemplate.convertAndSend("/topic/devices/" + deviceId + "/state", stateJson);
                log.info("📡 Broadcasted state to UI: device={}, state={}", deviceId, command);
            }
            
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to send command to device {}: {}", deviceId, e.getMessage());
            return false;
        }
    }
    
    public boolean sendCommand(String deviceId, String command) {
        try {
            return sendCommand(Long.parseLong(deviceId), command);
        } catch (NumberFormatException e) {
            log.warn("⚠️  Invalid deviceId format: {}", deviceId);
            return false;
        }
    }
}
