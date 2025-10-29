package com.iot.management.model.dto.device;

/**
 * DTO đại diện cho thiết bị với thông tin đơn giản
 */
public class DeviceDTO {
    private Long maThietBi;
    private String tenThietBi;
    private String loaiThietBi;      // Tên loại thiết bị (Temperature Sensor, Switch...)
    private String trangThai;        // ONLINE, OFFLINE, MAINTENANCE
    private String currentValue;     // Giá trị hiện tại (ON/OFF cho switch, 25°C cho sensor)
    private Boolean isControllable;  // Có thể điều khiển không (switch = true, sensor = false)

    public DeviceDTO() {
    }

    public DeviceDTO(Long maThietBi, String tenThietBi, String loaiThietBi, String trangThai) {
        this.maThietBi = maThietBi;
        this.tenThietBi = tenThietBi;
        this.loaiThietBi = loaiThietBi;
        this.trangThai = trangThai;
    }

    // Getters and Setters
    public Long getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

    public String getLoaiThietBi() {
        return loaiThietBi;
    }

    public void setLoaiThietBi(String loaiThietBi) {
        this.loaiThietBi = loaiThietBi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public Boolean getIsControllable() {
        return isControllable;
    }

    public void setIsControllable(Boolean isControllable) {
        this.isControllable = isControllable;
    }
}
