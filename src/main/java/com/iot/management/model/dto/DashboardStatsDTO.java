package com.iot.management.model.dto;

/**
 * DTO chứa các số liệu thống kê cho dashboard
 */
public class DashboardStatsDTO {
    private Long totalKhuVuc;        // Tổng số khu vực
    private Long totalThietBi;       // Tổng số thiết bị
    
    // Phân loại theo nhóm
    private Long totalControllers;   // Thiết bị điều khiển (LED, công tắc)
    private Long totalSensors;       // Cảm biến (nhiệt độ, độ ẩm)
    private Long totalActuators;     // Actuator (relay, motor, quạt)
    
    // Trạng thái
    private Long devicesOnline;      // Số thiết bị online
    private Long devicesOffline;     // Số thiết bị offline

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(Long totalKhuVuc, Long totalThietBi, Long totalControllers, 
                            Long totalSensors, Long totalActuators, Long devicesOnline, Long devicesOffline) {
        this.totalKhuVuc = totalKhuVuc;
        this.totalThietBi = totalThietBi;
        this.totalControllers = totalControllers;
        this.totalSensors = totalSensors;
        this.totalActuators = totalActuators;
        this.devicesOnline = devicesOnline;
        this.devicesOffline = devicesOffline;
    }

    // Getters and Setters
    public Long getTotalKhuVuc() {
        return totalKhuVuc;
    }

    public void setTotalKhuVuc(Long totalKhuVuc) {
        this.totalKhuVuc = totalKhuVuc;
    }

    public Long getTotalThietBi() {
        return totalThietBi;
    }

    public void setTotalThietBi(Long totalThietBi) {
        this.totalThietBi = totalThietBi;
    }

    public Long getTotalControllers() {
        return totalControllers;
    }

    public void setTotalControllers(Long totalControllers) {
        this.totalControllers = totalControllers;
    }

    public Long getTotalSensors() {
        return totalSensors;
    }

    public void setTotalSensors(Long totalSensors) {
        this.totalSensors = totalSensors;
    }

    public Long getTotalActuators() {
        return totalActuators;
    }

    public void setTotalActuators(Long totalActuators) {
        this.totalActuators = totalActuators;
    }

    public Long getDevicesOnline() {
        return devicesOnline;
    }

    public void setDevicesOnline(Long devicesOnline) {
        this.devicesOnline = devicesOnline;
    }

    public Long getDevicesOffline() {
        return devicesOffline;
    }

    public void setDevicesOffline(Long devicesOffline) {
        this.devicesOffline = devicesOffline;
    }
}
