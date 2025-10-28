package com.iot.management.model.entity;

import com.iot.management.model.enums.DuAnRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "LoiMoiDuAn")
public class LoiMoiDuAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_loi_moi")
    private Long maLoiMoi;

    @ManyToOne
    @JoinColumn(name = "ma_du_an", nullable = false)
    private DuAn duAn;

    @Column(name = "email_nguoi_nhan", nullable = false)
    private String emailNguoiNhan;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_moi", nullable = false)
    private NguoiDung nguoiMoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private DuAnRole vaiTro;

    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "ngay_het_han")
    private LocalDateTime ngayHetHan;

    @Column(name = "trang_thai")
    private String trangThai = "PENDING"; // PENDING, ACCEPTED, REJECTED, EXPIRED

    // Getters and Setters
    public Long getMaLoiMoi() {
        return maLoiMoi;
    }

    public void setMaLoiMoi(Long maLoiMoi) {
        this.maLoiMoi = maLoiMoi;
    }

    public DuAn getDuAn() {
        return duAn;
    }

    public void setDuAn(DuAn duAn) {
        this.duAn = duAn;
    }

    public String getEmailNguoiNhan() {
        return emailNguoiNhan;
    }

    public void setEmailNguoiNhan(String emailNguoiNhan) {
        this.emailNguoiNhan = emailNguoiNhan;
    }

    public NguoiDung getNguoiMoi() {
        return nguoiMoi;
    }

    public void setNguoiMoi(NguoiDung nguoiMoi) {
        this.nguoiMoi = nguoiMoi;
    }

    public DuAnRole getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(DuAnRole vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDateTime ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
