package com.iot.management.model.dto.backup;

public class LuatBackupDTO {
    private String tenTruong;
    private String lenhHanhDong;
    private String bieuThucLogic;
    private Integer thoiGianDuyTriDieuKien;
    private Boolean kichHoat;
    private String thietBiTen; // Tên thiết bị để match khi restore

    // Constructors
    public LuatBackupDTO() {}

    // Getters and Setters
    public String getTenTruong() { return tenTruong; }
    public void setTenTruong(String tenTruong) { this.tenTruong = tenTruong; }

    public String getLenhHanhDong() { return lenhHanhDong; }
    public void setLenhHanhDong(String lenhHanhDong) { this.lenhHanhDong = lenhHanhDong; }

    public String getBieuThucLogic() { return bieuThucLogic; }
    public void setBieuThucLogic(String bieuThucLogic) { this.bieuThucLogic = bieuThucLogic; }

    public Integer getThoiGianDuyTriDieuKien() { return thoiGianDuyTriDieuKien; }
    public void setThoiGianDuyTriDieuKien(Integer thoiGianDuyTriDieuKien) { 
        this.thoiGianDuyTriDieuKien = thoiGianDuyTriDieuKien; 
    }

    public Boolean getKichHoat() { return kichHoat; }
    public void setKichHoat(Boolean kichHoat) { this.kichHoat = kichHoat; }

    public String getThietBiTen() { return thietBiTen; }
    public void setThietBiTen(String thietBiTen) { this.thietBiTen = thietBiTen; }
}
