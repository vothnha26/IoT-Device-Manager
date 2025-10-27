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

    @Column(name = "ten_truong", nullable = false, length = 50)
    private String tenTruong;

    @Column(name = "phep_toan", nullable = false, length = 10)
    private String phepToan;

    @Column(name = "gia_tri_nguong", nullable = false, length = 255)
    private String giaTriNguong;

    @Column(name = "lenh_hanh_dong", length = 50)
    private String lenhHanhDong;

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

    public String getPhepToan() {
        return phepToan;
    }

    public void setPhepToan(String phepToan) {
        this.phepToan = phepToan;
    }

    public String getGiaTriNguong() {
        return giaTriNguong;
    }

    public void setGiaTriNguong(String giaTriNguong) {
        this.giaTriNguong = giaTriNguong;
    }

    public String getLenhHanhDong() {
        return lenhHanhDong;
    }

    public void setLenhHanhDong(String lenhHanhDong) {
        this.lenhHanhDong = lenhHanhDong;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }
}