package com.iot.management.model.dto.request;

public class TaoQuyenHeThongRequest {
    
    private String maNhom;
    private String tenQuyen;
    private String moTa;
    
    // Constructors
    public TaoQuyenHeThongRequest() {
    }
    
    public TaoQuyenHeThongRequest(String maNhom, String tenQuyen, String moTa) {
        this.maNhom = maNhom;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }
    
    // Getters and Setters
    public String getMaNhom() {
        return maNhom;
    }
    
    public void setMaNhom(String maNhom) {
        this.maNhom = maNhom;
    }
    
    public String getTenQuyen() {
        return tenQuyen;
    }
    
    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }
    
    public String getMoTa() {
        return moTa;
    }
    
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
