package com.iot.management.model.entity;

import com.iot.management.model.enums.DuAnRole;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhanQuyenThietBi")
public class PhanQuyenThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_phan_quyen_thiet_bi")
    private Long maPhanQuyenThietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private DuAnRole vaiTro;

    @Column(name = "ngay_cap_quyen")
    private LocalDateTime ngayCapQuyen;

    @Column(name = "co_quyen_dieu_khien")
    private Boolean coQuyenDieuKhien = false;

    @Column(name = "co_quyen_xem_du_lieu")
    private Boolean coQuyenXemDuLieu = false;

    @Column(name = "co_quyen_chinh_sua")
    private Boolean coQuyenChinhSua = false;

    public PhanQuyenThietBi() {
        this.ngayCapQuyen = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getMaPhanQuyenThietBi() {
        return maPhanQuyenThietBi;
    }

    public void setMaPhanQuyenThietBi(Long maPhanQuyenThietBi) {
        this.maPhanQuyenThietBi = maPhanQuyenThietBi;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
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

    public Boolean getCoQuyenDieuKhien() {
        return coQuyenDieuKhien;
    }

    public void setCoQuyenDieuKhien(Boolean coQuyenDieuKhien) {
        this.coQuyenDieuKhien = coQuyenDieuKhien;
    }

    public Boolean getCoQuyenXemDuLieu() {
        return coQuyenXemDuLieu;
    }

    public void setCoQuyenXemDuLieu(Boolean coQuyenXemDuLieu) {
        this.coQuyenXemDuLieu = coQuyenXemDuLieu;
    }

    public Boolean getCoQuyenChinhSua() {
        return coQuyenChinhSua;
    }

    public void setCoQuyenChinhSua(Boolean coQuyenChinhSua) {
        this.coQuyenChinhSua = coQuyenChinhSua;
    }
}
