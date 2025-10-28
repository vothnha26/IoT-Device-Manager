package com.iot.management.service;

import com.iot.management.model.entity.LuatNguong;
import com.iot.management.model.entity.NhatKyDuLieu;
import com.iot.management.model.entity.LichTrinh;
import java.util.List;

public interface TuDongHoaService {
    List<LichTrinh> getLichTrinhByThietBi(Long maThietBi);
    boolean existsById(Long id);
    LichTrinh toggleSchedule(Long id, boolean kichHoat);
    // Tạo hoặc cập nhật một luật ngưỡng
    LuatNguong saveRule(LuatNguong luatNguong);
    
    // Tạo hoặc cập nhật một lịch trình
    LichTrinh saveSchedule(LichTrinh lichTrinh);
    
    // Xóa một luật
    void deleteRule(Long ruleId);

    // Xóa một lịch trình
    void deleteSchedule(Long scheduleId);

    // Xử lý các luật cho một bản ghi dữ liệu vừa được lưu
    void processRules(NhatKyDuLieu dataLog);

    // Lấy danh sách lịch trình cho một thiết bị
    java.util.List<com.iot.management.model.entity.LichTrinh> findSchedulesByDevice(Long deviceId);

    // Lấy danh sách luật cho một thiết bị
    java.util.List<com.iot.management.model.entity.LuatNguong> findRulesByDevice(Long deviceId);

    // Lấy lịch trình theo ID
    java.util.Optional<com.iot.management.model.entity.LichTrinh> findScheduleById(Long scheduleId);

    // Lấy luật theo ID
    java.util.Optional<com.iot.management.model.entity.LuatNguong> findRuleById(Long ruleId);
}