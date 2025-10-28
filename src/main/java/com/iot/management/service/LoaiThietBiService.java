package com.iot.management.service;

import com.iot.management.model.entity.LoaiThietBi;
import java.util.List;
import java.util.Optional;

public interface LoaiThietBiService {
    // Tạo một loại thiết bị mới
    LoaiThietBi createDeviceType(LoaiThietBi loaiThietBi);
    
    // Lấy tất cả các loại thiết bị
    List<LoaiThietBi> findAllDeviceTypes();
    
    // Tìm loại thiết bị theo ID
    Optional<LoaiThietBi> findById(Long id);
    
    // Cập nhật loại thiết bị
    LoaiThietBi updateDeviceType(Long id, LoaiThietBi loaiThietBi);
    
    // Xóa loại thiết bị
    void deleteDeviceType(Long id);
}