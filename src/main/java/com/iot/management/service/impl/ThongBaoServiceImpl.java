package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.entity.ThongBao;
import com.iot.management.model.repository.ThongBaoRepository;
import com.iot.management.service.ThongBaoService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ThongBaoServiceImpl implements ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ThongBaoServiceImpl(ThongBaoRepository thongBaoRepository, 
                              SimpMessagingTemplate messagingTemplate) {
        this.thongBaoRepository = thongBaoRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public ThongBao createNotification(NguoiDung nguoiDung, String tieuDe, String noiDung, String loaiThongBao) {
        ThongBao thongBao = new ThongBao(nguoiDung, tieuDe, noiDung, loaiThongBao);
        thongBao = thongBaoRepository.save(thongBao);
        
        // Gửi thông báo realtime qua WebSocket
        sendRealtimeNotification(nguoiDung.getMaNguoiDung(), thongBao);
        
        return thongBao;
    }

    @Override
    public ThongBao createDeviceNotification(NguoiDung nguoiDung, ThietBi thietBi, String tieuDe, String noiDung, String loaiThongBao) {
        ThongBao thongBao = new ThongBao(nguoiDung, tieuDe, noiDung, loaiThongBao);
        thongBao.setThietBi(thietBi);
        thongBao.setUrlLienKet("/thiet-bi/khu-vuc/" + thietBi.getKhuVuc().getMaKhuVuc());
        thongBao = thongBaoRepository.save(thongBao);
        
        sendRealtimeNotification(nguoiDung.getMaNguoiDung(), thongBao);
        
        return thongBao;
    }

    @Override
    public ThongBao createAreaNotification(NguoiDung nguoiDung, KhuVuc khuVuc, String tieuDe, String noiDung, String loaiThongBao) {
        ThongBao thongBao = new ThongBao(nguoiDung, tieuDe, noiDung, loaiThongBao);
        thongBao.setKhuVuc(khuVuc);
        thongBao.setUrlLienKet("/thiet-bi/khu-vuc/" + khuVuc.getMaKhuVuc());
        thongBao = thongBaoRepository.save(thongBao);
        
        sendRealtimeNotification(nguoiDung.getMaNguoiDung(), thongBao);
        
        return thongBao;
    }

    @Override
    public void markAsRead(Long maThongBao) {
        ThongBao thongBao = thongBaoRepository.findById(maThongBao)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        thongBao.setDaDoc(true);
        thongBao.setThoiGianDoc(LocalDateTime.now());
        thongBaoRepository.save(thongBao);
    }

    @Override
    public void markAllAsRead(NguoiDung nguoiDung) {
        List<ThongBao> unreadNotifications = thongBaoRepository
                .findByNguoiDungAndDaDocOrderByThoiGianTaoDesc(nguoiDung, false);
        
        for (ThongBao thongBao : unreadNotifications) {
            thongBao.setDaDoc(true);
            thongBao.setThoiGianDoc(LocalDateTime.now());
        }
        
        thongBaoRepository.saveAll(unreadNotifications);
    }

    @Override
    public void deleteNotification(Long maThongBao) {
        thongBaoRepository.deleteById(maThongBao);
    }

    @Override
    public List<ThongBao> getAllByUser(NguoiDung nguoiDung) {
        return thongBaoRepository.findByNguoiDungOrderByThoiGianTaoDesc(nguoiDung);
    }

    @Override
    public List<ThongBao> getUnreadByUser(NguoiDung nguoiDung) {
        return thongBaoRepository.findByNguoiDungAndDaDocOrderByThoiGianTaoDesc(nguoiDung, false);
    }

    @Override
    public Long countUnread(NguoiDung nguoiDung) {
        return thongBaoRepository.countByNguoiDungAndDaDoc(nguoiDung, false);
    }

    @Override
    public List<ThongBao> getLatest(NguoiDung nguoiDung, int limit) {
        return thongBaoRepository.findTop10ByNguoiDungOrderByThoiGianTaoDesc(nguoiDung);
    }

    @Override
    public void deleteOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        thongBaoRepository.deleteOldNotifications(cutoffDate);
    }

    // Gửi thông báo realtime qua WebSocket
    private void sendRealtimeNotification(Long userId, ThongBao thongBao) {
        try {
            messagingTemplate.convertAndSend(
                "/topic/notifications/" + userId,
                thongBao
            );
        } catch (Exception e) {
            System.err.println("Failed to send realtime notification: " + e.getMessage());
        }
    }
}
