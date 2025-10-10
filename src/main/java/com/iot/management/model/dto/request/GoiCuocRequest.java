package com.iot.management.model.dto.request;

import java.math.BigDecimal;

// DTO này được dùng để nhận dữ liệu khi tạo hoặc cập nhật gói cước
public class GoiCuocRequest {

    private String tenGoi;
    private BigDecimal giaTien;
    private Integer slThietBiToiDa;
    private Integer slLuatToiDa;
    private Integer soNgayLuuDuLieu;

    // Getters and Setters
    public String getTenGoi() {
        return tenGoi;
    }

    public void setTenGoi(String tenGoi) {
        this.tenGoi = tenGoi;
    }

    public BigDecimal getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(BigDecimal giaTien) {
        this.giaTien = giaTien;
    }

    public Integer getSlThietBiToiDa() {
        return slThietBiToiDa;
    }

    public void setSlThietBiToiDa(Integer slThietBiToiDa) {
        this.slThietBiToiDa = slThietBiToiDa;
    }

    public Integer getSlLuatToiDa() {
        return slLuatToiDa;
    }

    public void setSlLuatToiDa(Integer slLuatToiDa) {
        this.slLuatToiDa = slLuatToiDa;
    }

    public Integer getSoNgayLuuDuLieu() {
        return soNgayLuuDuLieu;
    }

    public void setSoNgayLuuDuLieu(Integer soNgayLuuDuLieu) {
        this.soNgayLuuDuLieu = soNgayLuuDuLieu;
    }
}