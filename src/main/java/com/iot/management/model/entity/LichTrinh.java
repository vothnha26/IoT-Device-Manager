package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "LichTrinh")
public class LichTrinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lich_trinh")
    private Long maLichTrinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @Column(name = "ten_lich_trinh", length = 100)
    private String tenLichTrinh;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalTime thoiGianKetThuc;

    @Column(name = "lenh_khi_bat_dau", nullable = false, length = 50)
    private String lenhKhiBatDau;

    @Column(name = "lenh_khi_ket_thuc", nullable = false, length = 50)
    private String lenhKhiKetThuc;

    /**
     * Lưu các ngày trong tuần, ví dụ: "1,2,3,4,5" cho T2-T6, hoặc "*" cho mỗi ngày.
     * Chủ Nhật là 0, Thứ Hai là 1, ..., Thứ Bảy là 6.
     */
    @Column(name = "ngay_trong_tuan", nullable = false, length = 20)
    private String ngayTrongTuan;

    @Column(name = "kich_hoat", nullable = false)
    private boolean kichHoat = true;

    // Constructors
    public LichTrinh() {
    }

    // Getters and Setters
    public Long getMaLichTrinh() {
        return maLichTrinh;
    }

    public void setMaLichTrinh(Long maLichTrinh) {
        this.maLichTrinh = maLichTrinh;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public String getTenLichTrinh() {
        return tenLichTrinh;
    }

    public void setTenLichTrinh(String tenLichTrinh) {
        this.tenLichTrinh = tenLichTrinh;
    }

    public LocalTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public String getLenhKhiBatDau() {
        return lenhKhiBatDau;
    }

    public void setLenhKhiBatDau(String lenhKhiBatDau) {
        this.lenhKhiBatDau = lenhKhiBatDau;
    }

    public String getLenhKhiKetThuc() {
        return lenhKhiKetThuc;
    }

    public void setLenhKhiKetThuc(String lenhKhiKetThuc) {
        this.lenhKhiKetThuc = lenhKhiKetThuc;
    }

    public String getNgayTrongTuan() {
        return ngayTrongTuan;
    }

    public void setNgayTrongTuan(String ngayTrongTuan) {
        this.ngayTrongTuan = ngayTrongTuan;
    }

    public boolean isKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(boolean kichHoat) {
        this.kichHoat = kichHoat;
    }
}
