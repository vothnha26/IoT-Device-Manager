package com.iot.management.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class DeviceSessionRegistry {
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void register(String deviceId, WebSocketSession session) {
        if (deviceId == null || deviceId.isBlank() || session == null) return;
        sessions.put(deviceId, session);
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
}
