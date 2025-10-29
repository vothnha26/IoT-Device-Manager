package com.iot.management.config;

import com.iot.management.websocket.DeviceWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketDeviceConfig implements WebSocketConfigurer {

    private final DeviceWebSocketHandler deviceWebSocketHandler;

    public WebSocketDeviceConfig(DeviceWebSocketHandler deviceWebSocketHandler) {
        this.deviceWebSocketHandler = deviceWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        // Raw WebSocket endpoint for devices: ws://host:port/ws/device?deviceId={id}&token=...
        registry.addHandler(deviceWebSocketHandler, "/ws/device")
                .setAllowedOrigins("*");
    }
}
