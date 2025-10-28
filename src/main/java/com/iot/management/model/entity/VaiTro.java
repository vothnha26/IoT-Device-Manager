package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "VaiTro")
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_vai_tro")
    private Long maVaiTro;

    @Column(name = "ten_vai_tro", unique = true, nullable = false, length = 20)
    private String tenVaiTro;

    @OneToMany(mappedBy = "vaiTro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VaiTroQuyen> vaiTroQuyens = new HashSet<>();

    public Long getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(Long maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    // Keep compatibility with existing code which expects getName()/setName()
    public String getName() {
        return tenVaiTro;
    }

    public void setName(String name) {
        this.tenVaiTro = name;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    public Set<VaiTroQuyen> getVaiTroQuyens() {
        return vaiTroQuyens;
    }

    public void setVaiTroQuyens(Set<VaiTroQuyen> vaiTroQuyens) {
        this.vaiTroQuyens = vaiTroQuyens;
    }
}
