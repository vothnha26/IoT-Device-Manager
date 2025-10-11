package com.iot.management.service;

import com.iot.management.model.entity.KhuVuc;
import java.util.List;

public interface KhuVucService {
    // Tạo một khu vực mới (có thể là con của một khu vực khác)
    KhuVuc createLocation(Long ownerId, Long parentLocationId, KhuVuc khuVuc);

    // Lấy tất cả các khu vực gốc (cấp cao nhất) của một người dùng
    List<KhuVuc> findRootLocationsByOwner(Long ownerId);

    // Lấy tất cả các khu vực con của một khu vực cha
    List<KhuVuc> findChildLocations(Long parentLocationId);
    
    // Cập nhật thông tin khu vực
    KhuVuc updateLocation(Long ownerId, KhuVuc khuVuc);
}