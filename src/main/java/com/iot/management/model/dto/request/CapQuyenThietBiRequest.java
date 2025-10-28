package com.iot.management.model.dto.request;

import com.iot.management.model.enums.DuAnRole;

public class CapQuyenThietBiRequest {
    
    private Long maThietBi;
    private Long maNguoiDung;
    private DuAnRole vaiTro;
    private Boolean coQuyenDieuKhien;
    private Boolean coQuyenXemDuLieu;
    private Boolean coQuyenChinhSua;
    
    // Constructors
    public CapQuyenThietBiRequest() {
    }
    
    // Getters and Setters
    public Long getMaThietBi() {
        return maThietBi;
    }
    
    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
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
    
    public Boolean getCoQuyenDieuKhien() {
        return coQuyenDieuKhien;
    }
    
    public void setCoQuyenDieuKhien(Boolean coQuyenDieuKhien) {
        this.coQuyenDieuKhien = coQuyenDieuKhien;
    }
    
    public Boolean getCoQuyenXemDuLieu() {
        return coQuyenXemDuLieu;
    }
    
    public void setCoQuyenXemDuLieu(Boolean coQuyenXemDuLieu) {
        this.coQuyenXemDuLieu = coQuyenXemDuLieu;
    }
    
    public Boolean getCoQuyenChinhSua() {
        return coQuyenChinhSua;
    }
    
    public void setCoQuyenChinhSua(Boolean coQuyenChinhSua) {
        this.coQuyenChinhSua = coQuyenChinhSua;
    }
}
