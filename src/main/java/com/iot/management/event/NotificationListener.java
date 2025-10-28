package com.iot.management.event;

import com.iot.management.model.entity.ThongBao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        ThongBao thongBao = event.getThongBao();
        
        // Broadcast notification to specific user via WebSocket
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + thongBao.getNguoiDung().getMaNguoiDung(), 
            thongBao
        );
    }
}
