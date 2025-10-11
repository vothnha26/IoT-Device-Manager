package com.iot.management.service;

import com.iot.management.model.entity.NhatKyDuLieu;
import java.time.LocalDateTime;
import java.util.List;

public interface NhatKyDuLieuService {
    // Lưu một bản ghi dữ liệu mới từ thiết bị
    NhatKyDuLieu saveDataLog(String deviceToken, NhatKyDuLieu dataLog);

    // Lấy dữ liệu lịch sử của một thiết bị
    List<NhatKyDuLieu> getHistory(Long deviceId, LocalDateTime startTime, LocalDateTime endTime);

    // Lấy dữ liệu mới nhất của một thiết bị
    List<NhatKyDuLieu> getLatestData(Long deviceId);
}