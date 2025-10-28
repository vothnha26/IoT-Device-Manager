package com.iot.management.model.dto;

public class PackageUsageDTO {
    private int deviceUsed;
    private int deviceLimit;
    private int areaUsed;
    private int areaLimit;
    private int userUsed;
    private int userLimit;
    private long daysLeft;
    private String packageName;

    public PackageUsageDTO() {
    }

    public PackageUsageDTO(int deviceUsed, int deviceLimit, int areaUsed, int areaLimit, int userUsed, int userLimit, long daysLeft, String packageName) {
        this.deviceUsed = deviceUsed;
        this.deviceLimit = deviceLimit;
        this.areaUsed = areaUsed;
        this.areaLimit = areaLimit;
        this.userUsed = userUsed;
        this.userLimit = userLimit;
        this.daysLeft = daysLeft;
        this.packageName = packageName;
    }

    public int getDeviceUsed() {
        return deviceUsed;
    }

    public void setDeviceUsed(int deviceUsed) {
        this.deviceUsed = deviceUsed;
    }

    public int getDeviceLimit() {
        return deviceLimit;
    }

    public void setDeviceLimit(int deviceLimit) {
        this.deviceLimit = deviceLimit;
    }

    public int getAreaUsed() {
        return areaUsed;
    }

    public void setAreaUsed(int areaUsed) {
        this.areaUsed = areaUsed;
    }

    public int getAreaLimit() {
        return areaLimit;
    }

    public void setAreaLimit(int areaLimit) {
        this.areaLimit = areaLimit;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getUserUsed() {
        return userUsed;
    }

    public void setUserUsed(int userUsed) {
        this.userUsed = userUsed;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }
}
