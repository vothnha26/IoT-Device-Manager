package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NhatKyDuLieu")
public class NhatKyDuLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhat_ky")
    private Long maNhatKy;

    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "ten_truong", length = 150)
    private String tenTruong;

    @Column(name = "kieu_gia_tri", length = 50)
    private String kieuGiaTri;

    @Column(name = "gia_tri_chuoi", length = 2000)
    private String giaTriChuoi;

    @Column(name = "gia_tri_so")
    private Double giaTriSo;

    @Column(name = "gia_tri_logic")
    private Boolean giaTriLogic;

    // Getters/Setters
    public Long getMaNhatKy() { return maNhatKy; }
    public void setMaNhatKy(Long maNhatKy) { this.maNhatKy = maNhatKy; }

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public LocalDateTime getThoiGian() { return thoiGian; }
    public void setThoiGian(LocalDateTime thoiGian) { this.thoiGian = thoiGian; }

    public String getTenTruong() { return tenTruong; }
    public void setTenTruong(String tenTruong) { this.tenTruong = tenTruong; }

    public String getKieuGiaTri() { return kieuGiaTri; }
    public void setKieuGiaTri(String kieuGiaTri) { this.kieuGiaTri = kieuGiaTri; }

    public String getGiaTriChuoi() { return giaTriChuoi; }
    public void setGiaTriChuoi(String giaTriChuoi) { this.giaTriChuoi = giaTriChuoi; }

    public Double getGiaTriSo() { return giaTriSo; }
    public void setGiaTriSo(Double giaTriSo) { this.giaTriSo = giaTriSo; }

    public Boolean getGiaTriLogic() { return giaTriLogic; }
    public void setGiaTriLogic(Boolean giaTriLogic) { this.giaTriLogic = giaTriLogic; }
}
