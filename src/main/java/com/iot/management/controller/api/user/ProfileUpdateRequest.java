package com.iot.management.controller.api.user;

public class ProfileUpdateRequest {
    private String tenDangNhap;
    private String matKhau; // plain text password; service should encode
    private Boolean kichHoat;

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public Boolean getKichHoat() {
        return kichHoat;
    }

    public void setKichHoat(Boolean kichHoat) {
        this.kichHoat = kichHoat;
    }
}
