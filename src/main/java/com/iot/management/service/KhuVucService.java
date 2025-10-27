package com.iot.management.service;

import com.iot.management.model.entity.KhuVuc;
import java.util.List;

public interface KhuVucService {
    // Tạo một khu vực mới trong dự án
    KhuVuc createLocation(Long ownerId, Long duAnId, KhuVuc khuVuc, String moTa);
    
    // Lấy danh sách khu vực theo dự án
    List<KhuVuc> findByDuAn(Long duAnId);
    
    // Cập nhật thông tin khu vực
    KhuVuc updateLocation(Long ownerId, KhuVuc khuVuc);
    
    /**
     * Lấy tất cả khu vực của user
     */
    List<KhuVuc> getAllKhuVucsByUser(Long userId);
    
    /**
     * Lấy chi tiết một khu vực
     */
    KhuVuc getKhuVucById(Long id);
    
    /**
     * Xóa khu vực
     */
    void deleteKhuVuc(Long id);
}