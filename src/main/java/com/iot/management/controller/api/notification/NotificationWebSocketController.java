package com.iot.management.controller.api.notification;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {
    
    /**
     * Client gửi message đến /app/notifications/subscribe
     * Server sẽ gửi lại message xác nhận đăng ký thành công
     */
    @MessageMapping("/notifications/subscribe")
    @SendTo("/topic/notifications/status")
    public String subscribe(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        return "Subscribed successfully - Session: " + sessionId;
    }
    
    /**
     * Endpoint để client gửi yêu cầu đánh dấu đã đọc
     */
    @MessageMapping("/notifications/markAsRead")
    public void markAsRead(Long notificationId, SimpMessageHeaderAccessor headerAccessor) {
        // Xử lý đánh dấu đã đọc
        // Có thể inject ThongBaoService vào đây nếu cần
    }
}
