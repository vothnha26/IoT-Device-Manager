package com.iot.management.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "LuatNguong")
public class LuatNguong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_luat")
    private Long maLuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @Column(name = "ten_truong", columnDefinition = "NVARCHAR(50)")
    private String tenTruong;

    @Column(name = "lenh_hanh_dong", columnDefinition = "NVARCHAR(50)")
    private String lenhHanhDong;

    // Biểu thức logic tổng hợp, ví dụ: "nhiet_do > 30 AND do_am < 70"
    // Luật hiện chỉ sử dụng biểu thức; cặp (tenTruong, phepToan, giaTriNguong) đã bị loại bỏ.
    @Column(name = "bieu_thuc_logic", columnDefinition = "NVARCHAR(1000)")
    private String bieuThucLogic;

    // Điều kiện phải đúng liên tục X giây mới kích hoạt
    @Column(name = "thoi_gian_duy_tri_dieu_kien")
    private Integer thoiGianDuyTriDieuKien;

    @Column(name = "kich_hoat", nullable = false)
    private boolean kichHoat = true;

    public LuatNguong() {
    }

    public Long getMaLuat() {
        return maLuat;
    }

    public void setMaLuat(Long maLuat) {
        this.maLuat = maLuat;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public String getTenTruong() {
        return tenTruong;
    }

    public void setTenTruong(String tenTruong) {
        this.tenTruong = tenTruong;
    }

    public String getLenhHanhDong() {
        return lenhHanhDong;
    }

    public void setLenhHanhDong(String lenhHanhDong) {
        this.lenhHanhDong = lenhHanhDong;
    }

    public String getBieuThucLogic() {
        return bieuThucLogic;
    }

    public void setBieuThucLogic(String bieuThucLogic) {
        this.bieuThucLogic = bieuThucLogic;
    }

    public Integer getThoiGianDuyTriDieuKien() {
        return thoiGianDuyTriDieuKien;
    }

    public void setThoiGianDuyTriDieuKien(Integer thoiGianDuyTriDieuKien) {
        this.thoiGianDuyTriDieuKien = thoiGianDuyTriDieuKien;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }
}