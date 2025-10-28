package com.iot.management.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NguoiDung_device_permission")
public class UserDevicePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "NguoiDung_id", nullable = false)
    private NguoiDung nguoiDung;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private ThietBi device;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private NguoiDung createdBy;

    public UserDevicePermission() {
    }

    public UserDevicePermission(Long id, NguoiDung NguoiDung, ThietBi device, LocalDateTime createdAt, NguoiDung createdBy) {
        this.id = id;
        this.nguoiDung = NguoiDung;
        this.device = device;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung NguoiDung) {
        this.nguoiDung = NguoiDung;
    }

    public ThietBi getDevice() {
        return device;
    }

    public void setDevice(ThietBi device) {
        this.device = device;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public NguoiDung getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(NguoiDung createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "NguoiDungDevicePermission{" +
                "id=" + id +
                ", NguoiDung=" + nguoiDung +
                ", device=" + device +
                ", createdAt=" + createdAt +
                ", createdBy=" + createdBy +
                '}';
    }
}