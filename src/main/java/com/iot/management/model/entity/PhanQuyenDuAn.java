package com.iot.management.model.entity;

import com.iot.management.model.enums.DuAnRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhanQuyenDuAn")
public class PhanQuyenDuAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phan_quyen")
    private Long maPhanQuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_du_an", nullable = false)
    private DuAn duAn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private DuAnRole vaiTro;

    @Column(name = "ngay_cap_quyen")
    private LocalDateTime ngayCapQuyen;

    public PhanQuyenDuAn() {
        this.ngayCapQuyen = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMaPhanQuyen() {
        return maPhanQuyen;
    }

    public void setMaPhanQuyen(Long maPhanQuyen) {
        this.maPhanQuyen = maPhanQuyen;
    }

    public DuAn getDuAn() {
        return duAn;
    }

    public void setDuAn(DuAn duAn) {
        this.duAn = duAn;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public DuAnRole getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(DuAnRole vaiTro) {
        this.vaiTro = vaiTro;
    }

    public LocalDateTime getNgayCapQuyen() {
        return ngayCapQuyen;
    }

    public void setNgayCapQuyen(LocalDateTime ngayCapQuyen) {
        this.ngayCapQuyen = ngayCapQuyen;
    }
}