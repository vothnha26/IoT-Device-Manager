package com.iot.management.model.dto.backup;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ThietBiBackupDTO {
    private String tenThietBi;
    private String loaiThietBi;
    private String moTa;
    private String trangThai;
    private String khuVucTen; // Tên khu vực để match khi restore
    private LocalDate ngayLapDat;
    private LocalDateTime lanHoatDongCuoi;

    // Constructors
    public ThietBiBackupDTO() {}

    // Getters and Setters
    public String getTenThietBi() { return tenThietBi; }
    public void setTenThietBi(String tenThietBi) { this.tenThietBi = tenThietBi; }

    public String getLoaiThietBi() { return loaiThietBi; }
    public void setLoaiThietBi(String loaiThietBi) { this.loaiThietBi = loaiThietBi; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getKhuVucTen() { return khuVucTen; }
    public void setKhuVucTen(String khuVucTen) { this.khuVucTen = khuVucTen; }

    public LocalDate getNgayLapDat() { return ngayLapDat; }
    public void setNgayLapDat(LocalDate ngayLapDat) { this.ngayLapDat = ngayLapDat; }

    public LocalDateTime getLanHoatDongCuoi() { return lanHoatDongCuoi; }
    public void setLanHoatDongCuoi(LocalDateTime lanHoatDongCuoi) { this.lanHoatDongCuoi = lanHoatDongCuoi; }
}
