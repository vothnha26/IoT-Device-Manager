package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LenhDieuKhien")
public class LenhDieuKhien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lenh")
    private Long maLenh;

    @Column(name = "ma_thiet_bi")
    private Long maThietBi;

    @Column(name = "ten_lenh", length = 200)
    private String tenLenh;

    @Column(name = "gia_tri_lenh", length = 1000)
    private String giaTriLenh;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "ma_nguoi_gui")
    private Long maNguoiGui;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // Getters/Setters
    public Long getMaLenh() { return maLenh; }
    public void setMaLenh(Long maLenh) { this.maLenh = maLenh; }

    public Long getMaThietBi() { return maThietBi; }
    public void setMaThietBi(Long maThietBi) { this.maThietBi = maThietBi; }

    public String getTenLenh() { return tenLenh; }
    public void setTenLenh(String tenLenh) { this.tenLenh = tenLenh; }

    public String getGiaTriLenh() { return giaTriLenh; }
    public void setGiaTriLenh(String giaTriLenh) { this.giaTriLenh = giaTriLenh; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Long getMaNguoiGui() { return maNguoiGui; }
    public void setMaNguoiGui(Long maNguoiGui) { this.maNguoiGui = maNguoiGui; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}
