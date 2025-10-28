package com.iot.management.model.dto.request;

public class CapQuyenKhuVucRequest {
    
    private Long maKhuVuc;
    private Long maNguoiDung;
    private String vaiTro; // 'QUAN_LY_KHU_VUC' hoặc 'XEM'
    
    // Constructors
    public CapQuyenKhuVucRequest() {
    }
    
    public CapQuyenKhuVucRequest(Long maKhuVuc, Long maNguoiDung, String vaiTro) {
        this.maKhuVuc = maKhuVuc;
        this.maNguoiDung = maNguoiDung;
        this.vaiTro = vaiTro;
    }
    
    // Getters and Setters
    public Long getMaKhuVuc() {
        return maKhuVuc;
    }
    
    public void setMaKhuVuc(Long maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }
    
    public Long getMaNguoiDung() {
        return maNguoiDung;
    }
    
    public void setMaNguoiDung(Long maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }
    
    public String getVaiTro() {
        return vaiTro;
    }
    
    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }
}
