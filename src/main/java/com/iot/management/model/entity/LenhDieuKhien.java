package com.iot.management.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "LenhDieuKhien")
public class LenhDieuKhien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_lenh")
    private Long maLenh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @Column(name = "ten_lenh", nullable = false, length = 50)
    private String tenLenh;

    @Column(name = "gia_tri_lenh", length = 100)
    private String giaTriLenh;

    @Column(name = "trang_thai", nullable = false, length = 20)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_gui")
    private NguoiDung nguoiGui;

    @CreationTimestamp
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    public LenhDieuKhien() {
    }

    public Long getMaLenh() {
        return maLenh;
    }

    public void setMaLenh(Long maLenh) {
        this.maLenh = maLenh;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public String getTenLenh() {
        return tenLenh;
    }

    public void setTenLenh(String tenLenh) {
        this.tenLenh = tenLenh;
    }

    public String getGiaTriLenh() {
        return giaTriLenh;
    }

    public void setGiaTriLenh(String giaTriLenh) {
        this.giaTriLenh = giaTriLenh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public NguoiDung getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(NguoiDung nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}