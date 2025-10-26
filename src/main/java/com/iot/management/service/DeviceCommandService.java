package com.iot.management.service;

import com.iot.management.websocket.DeviceSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class DeviceCommandService {

    private static final Logger log = LoggerFactory.getLogger(DeviceCommandService.class);

    private final DeviceSessionRegistry sessionRegistry;

    public DeviceCommandService(DeviceSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * Send a toggle command to device over WebSocket.
     * @param deviceId device id
     * @param trangThai expected values: "hoat_dong" or "tat"
     * @return true if delivered to an online session, false if offline/not delivered
     */
    public boolean sendToggleCommand(Long deviceId, String trangThai) {
        WebSocketSession session = sessionRegistry.getSession(deviceId);
        if (session == null || !session.isOpen()) {
            log.warn("Device {} is offline or session closed", deviceId);
            return false;
        }

        String cmd;
        if (trangThai == null) {
            return false;
        }
        switch (trangThai.trim().toLowerCase()) {
            case "hoat_dong":
                cmd = "ON";
                break;
            case "tat":
                cmd = "OFF";
                break;
            default:
                // fallback: accept direct ON/OFF
                cmd = trangThai.trim().toUpperCase();
        }

        try {
            session.sendMessage(new TextMessage(cmd));
            log.info("Sent command '{}' to device {} via WS", cmd, deviceId);
            return true;
        } catch (Exception e) {
            log.error("Failed to send WS command to device {}: {}", deviceId, e.getMessage());
            return false;
        }
    }
}
