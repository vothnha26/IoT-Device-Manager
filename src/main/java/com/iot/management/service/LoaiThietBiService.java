package com.iot.management.service;

import com.iot.management.model.entity.LoaiThietBi;
import java.util.List;

public interface LoaiThietBiService {
    // Tạo một loại thiết bị mới
    LoaiThietBi createDeviceType(LoaiThietBi loaiThietBi);
    
    // Lấy tất cả các loại thiết bị
    List<LoaiThietBi> findAllDeviceTypes();
}