package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "NhatKyDuLieu")
public class NhatKyDuLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhat_ky")
    private Long maNhatKy;
    
    // Giữ lại trường đối tượng này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thiet_bi", nullable = false)
    private ThietBi thietBi;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    // Hỗ trợ nhận maThietBi trực tiếp từ JSON request body (không map vào relationship)
    @Transient
    @JsonProperty("maThietBi")
    private Long maThietBi;

    @Column(name = "ten_truong", nullable = false, length = 50)
    private String tenTruong;

    @Column(name = "kieu_gia_tri", nullable = false)
    private byte kieuGiaTri;

    @Column(name = "gia_tri_chuoi")
    private String giaTriChuoi;

    @Column(name = "gia_tri_so")
    private BigDecimal giaTriSo;

    @Column(name = "gia_tri_logic")
    private Boolean giaTriLogic;

    // Constructors
    public NhatKyDuLieu() {
    }

    // Getters and Setters
    public Long getMaNhatKy() {
        return maNhatKy;
    }

    public void setMaNhatKy(Long maNhatKy) {
        this.maNhatKy = maNhatKy;
    }

    public ThietBi getThietBi() {
        return thietBi;
    }

    public void setThietBi(ThietBi thietBi) {
        this.thietBi = thietBi;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getTenTruong() {
        return tenTruong;
    }

    public void setTenTruong(String tenTruong) {
        this.tenTruong = tenTruong;
    }

    public byte getKieuGiaTri() {
        return kieuGiaTri;
    }

    public void setKieuGiaTri(byte kieuGiaTri) {
        this.kieuGiaTri = kieuGiaTri;
    }

    public String getGiaTriChuoi() {
        return giaTriChuoi;
    }

    public void setGiaTriChuoi(String giaTriChuoi) {
        this.giaTriChuoi = giaTriChuoi;
    }

    public BigDecimal getGiaTriSo() {
        return giaTriSo;
    }

    public void setGiaTriSo(BigDecimal giaTriSo) {
        this.giaTriSo = giaTriSo;
    }

    public Boolean getGiaTriLogic() {
        return giaTriLogic;
    }

    public void setGiaTriLogic(Boolean giaTriLogic) {
        this.giaTriLogic = giaTriLogic;
    }

    public Long getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(Long maThietBi) {
        this.maThietBi = maThietBi;
    }
    
    /**
     * Helper method để lấy giá trị dựa trên kiểu dữ liệu
     */
    public String getGiaTri() {
        if (kieuGiaTri == 0 && giaTriChuoi != null) {
            return giaTriChuoi;
        } else if (kieuGiaTri == 1 && giaTriSo != null) {
            return giaTriSo.toString();
        } else if (kieuGiaTri == 2 && giaTriLogic != null) {
            return giaTriLogic.toString();
        }
        return null;
    }
}