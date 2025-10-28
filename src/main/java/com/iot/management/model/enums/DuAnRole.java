package com.iot.management.model.enums;

public enum DuAnRole {
    CHU_SO_HUU("Chủ sở hữu"),
    QUAN_LY("Quản lý"),
    NGUOI_DUNG("Người dùng");

    private final String description;

    DuAnRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}