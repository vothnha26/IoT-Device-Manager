package com.iot.management.model.entity;

/**
 * Enum định nghĩa các nhóm thiết bị trong hệ thống IoT
 */
public enum NhomThietBi {
    /**
     * Thiết bị điều khiển - có thể bật/tắt hoặc điều chỉnh
     * VD: Đèn LED, công tắc, relay, quạt
     */
    CONTROLLER("Thiết bị điều khiển"),
    
    /**
     * Cảm biến - thu thập dữ liệu môi trường
     * VD: Cảm biến nhiệt độ, độ ẩm, ánh sáng, chuyển động
     */
    SENSOR("Cảm biến"),
    
    /**
     * Thiết bị chấp hành - thực hiện hành động cơ học
     * VD: Motor, servo, van nước, cửa tự động
     */
    ACTUATOR("Thiết bị chấp hành");

    private final String moTa;

    NhomThietBi(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
