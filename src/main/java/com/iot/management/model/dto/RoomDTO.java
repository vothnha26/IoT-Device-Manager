package com.iot.management.model.dto;

import java.util.List;

/**
 * DTO đại diện cho một phòng/khu vực với danh sách thiết bị
 */
public class RoomDTO {
    private Long maKhuVuc;
    private String tenKhuVuc;
    private String loaiKhuVuc;
    private Double currentTemp;      // Nhiệt độ hiện tại
    private Double currentHumidity;  // Độ ẩm hiện tại
    private String lastUpdated;      // Thời gian cập nhật cuối
    private List<DeviceDTO> devices; // Danh sách thiết bị trong khu vực
    
    // Thống kê theo nhóm thiết bị trong khu vực này
    private Long controllerCount;    // Số thiết bị điều khiển
    private Long sensorCount;        // Số cảm biến
    private Long actuatorCount;      // Số actuator

    public RoomDTO() {
    }

    // Getters and Setters
    public Long getMaKhuVuc() {
        return maKhuVuc;
    }

    public void setMaKhuVuc(Long maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }

    public String getLoaiKhuVuc() {
        return loaiKhuVuc;
    }

    public void setLoaiKhuVuc(String loaiKhuVuc) {
        this.loaiKhuVuc = loaiKhuVuc;
    }

    public Double getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(Double currentTemp) {
        this.currentTemp = currentTemp;
    }

    public Double getCurrentHumidity() {
        return currentHumidity;
    }

    public void setCurrentHumidity(Double currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public List<DeviceDTO> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceDTO> devices) {
        this.devices = devices;
    }

    public Long getControllerCount() {
        return controllerCount;
    }

    public void setControllerCount(Long controllerCount) {
        this.controllerCount = controllerCount;
    }

    public Long getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(Long sensorCount) {
        this.sensorCount = sensorCount;
    }

    public Long getActuatorCount() {
        return actuatorCount;
    }

    public void setActuatorCount(Long actuatorCount) {
        this.actuatorCount = actuatorCount;
    }
}
