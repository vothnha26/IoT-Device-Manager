package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_quyen_khu_vuc")
public class PhanQuyenKhuVuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phan_quyen")
    private Long maPhanQuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_khu_vuc", nullable = false)
    private KhuVuc khuVuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Column(name = "vai_tro", nullable = false, columnDefinition = "NVARCHAR(20)")
    private String vaiTro; // 'QUAN_LY_KHU_VUC' hoặc 'XEM'

    @Column(name = "ngay_cap_quyen")
    private LocalDateTime ngayCapQuyen;

    public PhanQuyenKhuVuc() {
        this.ngayCapQuyen = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMaPhanQuyen() {
        return maPhanQuyen;
    }

    public void setMaPhanQuyen(Long maPhanQuyen) {
        this.maPhanQuyen = maPhanQuyen;
    }

    public KhuVuc getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVuc khuVuc) {
        this.khuVuc = khuVuc;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public LocalDateTime getNgayCapQuyen() {
        return ngayCapQuyen;
    }

    public void setNgayCapQuyen(LocalDateTime ngayCapQuyen) {
        this.ngayCapQuyen = ngayCapQuyen;
    }
}
