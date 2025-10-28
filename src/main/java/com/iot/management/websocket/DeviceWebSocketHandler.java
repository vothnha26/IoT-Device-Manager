package com.iot.management.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceWebSocketHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(DeviceWebSocketHandler.class);

    private final DeviceSessionRegistry registry;

    // Optional: only present if STOMP broker is configured
    @Autowired(required = false)
    @Nullable
    private SimpMessagingTemplate messagingTemplate;

    public DeviceWebSocketHandler(DeviceSessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        var uri = session.getUri();
        String query = uri != null ? uri.getQuery() : null;
        Map<String, String> params = parseQueryParams(query);
        String deviceId = params.get("deviceId");
        if (deviceId == null || deviceId.isBlank()) {
            log.warn("Rejecting WS connection without deviceId");
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        // TODO: validate token if provided: params.get("token")
        registry.register(deviceId, session);
        log.info("Device {} connected via WebSocket", deviceId);
        // Notify UI device online if STOMP available
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/devices/" + deviceId + "/status", "online");
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        var uri = session.getUri();
        String query = uri != null ? uri.getQuery() : null;
        Map<String, String> params = parseQueryParams(query);
        String deviceId = params.get("deviceId");
        String payload = message.getPayload();
        log.debug("Recv from device {}: {}", deviceId, payload);
        
        // ESP32 gửi: "hoat_dong" hoặc "tat"
        String state = payload != null ? payload.trim() : "";
        
        // Echo state upstream to UI if STOMP available
        var template = messagingTemplate;
        if (template != null && deviceId != null) {
            // Wrap in JSON format for frontend
            String body = "{\"deviceId\":\"" + deviceId + "\",\"state\":\"" + state + "\"}";
            template.convertAndSend("/topic/devices/" + deviceId + "/state", body);
            log.info("📤 Broadcasted to UI: device={}, state={}", deviceId, state);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        var uri = session.getUri();
        String query = uri != null ? uri.getQuery() : null;
        Map<String, String> params = parseQueryParams(query);
        String deviceId = params.get("deviceId");
        if (deviceId != null) {
            registry.unregister(deviceId, session);
            log.info("Device {} disconnected: {}", deviceId, status);
            var template = messagingTemplate;
            if (template != null) {
                template.convertAndSend("/topic/devices/" + deviceId + "/status", "offline");
            }
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                map.put(urlDecode(kv[0]), urlDecode(kv[1]));
            }
        }
        return map;
    }

    private String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }
}
