package com.iot.management.model.dto.request;

import com.iot.management.model.enums.DuAnRole;

public class CapQuyenDuAnRequest {
    
    private Long maDuAn;
    private Long maNguoiDung;
    private DuAnRole vaiTro;
    
    // Constructors
    public CapQuyenDuAnRequest() {
    }
    
    public CapQuyenDuAnRequest(Long maDuAn, Long maNguoiDung, DuAnRole vaiTro) {
        this.maDuAn = maDuAn;
        this.maNguoiDung = maNguoiDung;
        this.vaiTro = vaiTro;
    }
    
    // Getters and Setters
    public Long getMaDuAn() {
        return maDuAn;
    }
    
    public void setMaDuAn(Long maDuAn) {
        this.maDuAn = maDuAn;
    }
    
    public Long getMaNguoiDung() {
        return maNguoiDung;
    }
    
    public void setMaNguoiDung(Long maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }
    
    public DuAnRole getVaiTro() {
        return vaiTro;
    }
    
    public void setVaiTro(DuAnRole vaiTro) {
        this.vaiTro = vaiTro;
    }
}
