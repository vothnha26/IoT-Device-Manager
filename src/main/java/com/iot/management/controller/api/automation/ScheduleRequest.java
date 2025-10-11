package com.iot.management.controller.api.automation;

public class ScheduleRequest {
    private String tenLichTrinh;
    private String thoiGianBatDau; // accept "HH:mm" or ISO datetime
    private String thoiGianKetThuc;
    private String lenhKhiBatDau;
    private String lenhKhiKetThuc;
    private String hanhDong; // alternative name used by clients
    private String ngayTrongTuan;
    private Boolean kichHoat;
    private Long maThietBi;

    public String getTenLichTrinh() { return tenLichTrinh; }
    public void setTenLichTrinh(String tenLichTrinh) { this.tenLichTrinh = tenLichTrinh; }

    public String getThoiGianBatDau() { return thoiGianBatDau; }
    public void setThoiGianBatDau(String thoiGianBatDau) { this.thoiGianBatDau = thoiGianBatDau; }

    public String getThoiGianKetThuc() { return thoiGianKetThuc; }
    public void setThoiGianKetThuc(String thoiGianKetThuc) { this.thoiGianKetThuc = thoiGianKetThuc; }

    public String getLenhKhiBatDau() { return lenhKhiBatDau; }
    public void setLenhKhiBatDau(String lenhKhiBatDau) { this.lenhKhiBatDau = lenhKhiBatDau; }

    public String getLenhKhiKetThuc() { return lenhKhiKetThuc; }
    public void setLenhKhiKetThuc(String lenhKhiKetThuc) { this.lenhKhiKetThuc = lenhKhiKetThuc; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public String getNgayTrongTuan() { return ngayTrongTuan; }
    public void setNgayTrongTuan(String ngayTrongTuan) { this.ngayTrongTuan = ngayTrongTuan; }

    public Boolean getKichHoat() { return kichHoat; }
    public void setKichHoat(Boolean kichHoat) { this.kichHoat = kichHoat; }

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }
}
