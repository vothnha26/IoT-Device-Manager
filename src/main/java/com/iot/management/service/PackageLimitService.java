package com.iot.management.service;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.DangKyGoi;

public interface PackageLimitService {
    void validateDeviceLimit(DuAn duAn);
    void validateZoneLimit(DuAn duAn);
    void validateRuleLimit(DuAn duAn);
    DangKyGoi getCurrentPackage(DuAn duAn);
    boolean isPackageExpired(DangKyGoi dangKyGoi);
    DangKyGoi validatePackageStatus(DuAn duAn);
}