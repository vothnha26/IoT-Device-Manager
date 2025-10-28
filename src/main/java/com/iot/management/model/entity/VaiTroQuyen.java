package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "vai_tro_quyen")
@IdClass(VaiTroQuyen.VaiTroQuyenId.class)
public class VaiTroQuyen {

    @Id
    @Column(name = "ma_vai_tro")
    private Long maVaiTro;

    @Id
    @Column(name = "ma_quyen")
    private Long maQuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_vai_tro", insertable = false, updatable = false)
    private VaiTro vaiTro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_quyen", insertable = false, updatable = false)
    private QuyenHeThong quyenHeThong;

    // Constructors
    public VaiTroQuyen() {
    }

    public VaiTroQuyen(Long maVaiTro, Long maQuyen) {
        this.maVaiTro = maVaiTro;
        this.maQuyen = maQuyen;
    }

    // Getters and Setters
    public Long getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(Long maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public Long getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(Long maQuyen) {
        this.maQuyen = maQuyen;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public QuyenHeThong getQuyenHeThong() {
        return quyenHeThong;
    }

    public void setQuyenHeThong(QuyenHeThong quyenHeThong) {
        this.quyenHeThong = quyenHeThong;
    }

    // Composite Key Class
    public static class VaiTroQuyenId implements Serializable {
        private Long maVaiTro;
        private Long maQuyen;

        public VaiTroQuyenId() {
        }

        public VaiTroQuyenId(Long maVaiTro, Long maQuyen) {
            this.maVaiTro = maVaiTro;
            this.maQuyen = maQuyen;
        }

        public Long getMaVaiTro() {
            return maVaiTro;
        }

        public void setMaVaiTro(Long maVaiTro) {
            this.maVaiTro = maVaiTro;
        }

        public Long getMaQuyen() {
            return maQuyen;
        }

        public void setMaQuyen(Long maQuyen) {
            this.maQuyen = maQuyen;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VaiTroQuyenId that = (VaiTroQuyenId) o;
            return maVaiTro.equals(that.maVaiTro) && maQuyen.equals(that.maQuyen);
        }

        @Override
        public int hashCode() {
            return 31 * maVaiTro.hashCode() + maQuyen.hashCode();
        }
    }
}
