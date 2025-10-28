package com.iot.management.service;

import com.iot.management.model.entity.CauHinhHeThong;
import java.util.List;
import java.util.Optional;

public interface CauHinhHeThongService {
    
    // Lấy tất cả cấu hình
    List<CauHinhHeThong> getAllCauHinh();
    
    // Lấy cấu hình theo tên
    Optional<CauHinhHeThong> getCauHinhByTen(String tenCauHinh);
    
    // Lấy giá trị cấu hình (trả về default nếu không tìm thấy)
    String getGiaTri(String tenCauHinh, String defaultValue);
    
    // Lưu hoặc cập nhật cấu hình
    CauHinhHeThong saveCauHinh(String tenCauHinh, String giaTriCauHinh);
    
    // Xóa cấu hình
    void deleteCauHinh(String tenCauHinh);
    
    // Khởi tạo cấu hình mặc định
    void initDefaultConfig();
}
