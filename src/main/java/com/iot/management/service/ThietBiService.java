package com.iot.management.service;

import com.iot.management.model.entity.ThietBi;
import java.util.List;
import java.util.Optional;

public interface ThietBiService {
    // Tạo một thiết bị mới cho người dùng
    ThietBi createDevice(Long ownerId, ThietBi thietBi);

    // Lấy tất cả thiết bị của một người dùng
    List<ThietBi> findDevicesByOwner(Long ownerId);

    // Lấy thông tin chi tiết của một thiết bị bằng ID
    Optional<ThietBi> findDeviceById(Long deviceId);

    // Xóa một thiết bị
    void deleteDevice(Long deviceId);

    // Cập nhật trạng thái của thiết bị
    void capNhatTrangThaiThietBi(Long deviceId, String trangThai);
    
    // Cập nhật thông tin thiết bị
    ThietBi updateDevice(Long deviceId, ThietBi thietBi);
}