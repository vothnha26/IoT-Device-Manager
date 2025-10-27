package com.iot.management.model.enums;

public enum ThietBiPermission {
    VIEW("Xem"),
    CONTROL("Điều khiển"),
    MANAGE("Quản lý");

    private final String displayName;

    ThietBiPermission(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
