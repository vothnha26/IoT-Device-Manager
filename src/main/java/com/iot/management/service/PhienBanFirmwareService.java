package com.iot.management.service;

import com.iot.management.model.entity.PhienBanFirmware;

import java.util.List;
import java.util.Optional;

public interface PhienBanFirmwareService {

    /**
     * Lưu hoặc cập nhật một phiên bản firmware.
     * @param firmware Đối tượng PhienBanFirmware cần lưu.
     * @return Đối tượng PhienBanFirmware đã được lưu.
     */
    PhienBanFirmware save(PhienBanFirmware firmware);

    /**
     * Tìm tất cả các phiên bản firmware cho một loại thiết bị, sắp xếp từ mới nhất đến cũ nhất.
     * @param deviceTypeId ID của loại thiết bị.
     * @return Danh sách các phiên bản firmware.
     */
    List<PhienBanFirmware> findAllByDeviceType(Long deviceTypeId);

    /**
     * Tìm phiên bản firmware mới nhất cho một loại thiết bị.
     * @param deviceTypeId ID của loại thiết bị.
     * @return Optional chứa phiên bản mới nhất nếu có.
     */
    Optional<PhienBanFirmware> findLatestByDeviceType(Long deviceTypeId);

    /**
     * Xóa một phiên bản firmware theo ID.
     * @param id ID của phiên bản firmware.
     */
    void deleteById(Long id);
}