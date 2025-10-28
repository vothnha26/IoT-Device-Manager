package com.iot.management.service;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.entity.ThongBao;

import java.util.List;

public interface ThongBaoService {

    // Tạo thông báo mới
    ThongBao createNotification(NguoiDung nguoiDung, String tieuDe, String noiDung, String loaiThongBao);

    // Tạo thông báo liên quan đến thiết bị
    ThongBao createDeviceNotification(NguoiDung nguoiDung, ThietBi thietBi, String tieuDe, String noiDung, String loaiThongBao);

    // Tạo thông báo liên quan đến khu vực
    ThongBao createAreaNotification(NguoiDung nguoiDung, KhuVuc khuVuc, String tieuDe, String noiDung, String loaiThongBao);

    // Đánh dấu đã đọc
    void markAsRead(Long maThongBao);

    // Đánh dấu tất cả đã đọc
    void markAllAsRead(NguoiDung nguoiDung);

    // Xóa thông báo
    void deleteNotification(Long maThongBao);

    // Lấy tất cả thông báo của user
    List<ThongBao> getAllByUser(NguoiDung nguoiDung);

    // Lấy thông báo chưa đọc
    List<ThongBao> getUnreadByUser(NguoiDung nguoiDung);

    // Đếm thông báo chưa đọc
    Long countUnread(NguoiDung nguoiDung);

    // Lấy N thông báo mới nhất
    List<ThongBao> getLatest(NguoiDung nguoiDung, int limit);

    // Xóa thông báo cũ (auto cleanup)
    void deleteOldNotifications(int daysOld);
}
