package com.iot.management.service;

import com.iot.management.model.dto.DashboardStatsDTO;
import com.iot.management.model.dto.RoomDTO;
import java.util.List;

/**
 * Service xử lý logic cho Dashboard
 */
public interface DashboardService {
    
    /**
     * Lấy thống kê tổng quan cho user
     */
    DashboardStatsDTO getDashboardStats(Long userId);
    
    /**
     * Lấy danh sách phòng/khu vực với thiết bị của user
     */
    List<RoomDTO> getRoomsWithDevices(Long userId);
    
    /**
     * Lấy thông tin chi tiết một phòng
     */
    RoomDTO getRoomDetail(Long roomId);
}
