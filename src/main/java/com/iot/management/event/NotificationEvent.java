package com.iot.management.event;

import com.iot.management.model.entity.ThongBao;
import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {
    
    private final ThongBao thongBao;
    
    public NotificationEvent(Object source, ThongBao thongBao) {
        super(source);
        this.thongBao = thongBao;
    }
    
    public ThongBao getThongBao() {
        return thongBao;
    }
}
