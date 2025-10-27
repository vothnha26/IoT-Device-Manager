package com.iot.management.controller.api.automation;

public class RuleRequest {
    private Long maThietBi;
    private String tenTruong;
    private String lenhHanhDong;
    // Expression-based rule (optional)
    private String bieuThucLogic;
    private Integer thoiGianDuyTriDieuKien; // seconds
    // alternative fields accepted from clients
    private String loaiLuat; // e.g. GREATER_THAN
    private Double giaTri; // numeric threshold
    private String hanhDong; // alias for lenhHanhDong
    private Boolean kichHoat;

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public String getTenTruong() { return tenTruong; }
    public void setTenTruong(String tenTruong) { this.tenTruong = tenTruong; }

    public String getLenhHanhDong() { return lenhHanhDong; }
    public void setLenhHanhDong(String lenhHanhDong) { this.lenhHanhDong = lenhHanhDong; }

    public String getBieuThucLogic() { return bieuThucLogic; }
    public void setBieuThucLogic(String bieuThucLogic) { this.bieuThucLogic = bieuThucLogic; }

    public Integer getThoiGianDuyTriDieuKien() { return thoiGianDuyTriDieuKien; }
    public void setThoiGianDuyTriDieuKien(Integer thoiGianDuyTriDieuKien) { this.thoiGianDuyTriDieuKien = thoiGianDuyTriDieuKien; }

    public String getLoaiLuat() { return loaiLuat; }
    public void setLoaiLuat(String loaiLuat) { this.loaiLuat = loaiLuat; }

    public Double getGiaTri() { return giaTri; }
    public void setGiaTri(Double giaTri) { this.giaTri = giaTri; }

    public String getHanhDong() { return hanhDong; }
    public void setHanhDong(String hanhDong) { this.hanhDong = hanhDong; }

    public Boolean getKichHoat() { return kichHoat; }
    public void setKichHoat(Boolean kichHoat) { this.kichHoat = kichHoat; }
}
