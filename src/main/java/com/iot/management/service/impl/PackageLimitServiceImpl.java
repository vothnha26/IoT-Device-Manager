package com.iot.management.service.impl;

import com.iot.management.exception.PackageExpiredException;
import com.iot.management.exception.PackageLimitExceededException;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.service.PackageLimitService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PackageLimitServiceImpl implements PackageLimitService {

    @Override
    public void validateDeviceLimit(DuAn duAn) {
        DangKyGoi dangKyGoi = validatePackageStatus(duAn);
        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();

        if (goiCuoc == null) {
            throw new PackageExpiredException("Gói cước không hợp lệ");
        }

        // Đếm số thiết bị hiện tại trong tất cả khu vực của dự án
        int currentDevices = duAn.getKhuVucs().stream()
                .mapToInt(khuVuc -> khuVuc.getThietBis() != null ? khuVuc.getThietBis().size() : 0)
                .sum();

        int maxDevices = goiCuoc.getSlThietBiToiDa();

        System.out.println("🔍 Checking device limit for project: " + duAn.getMaDuAn());
        System.out.println("   Current devices: " + currentDevices + " / Max: " + maxDevices);
        System.out.println("   Package: " + goiCuoc.getTenGoi());

        if (currentDevices >= maxDevices) {
            throw new PackageLimitExceededException(
                    "Số lượng thiết bị đã đạt giới hạn của gói cước (" +
                            maxDevices + " thiết bị). Hiện tại: " + currentDevices + " thiết bị");
        }
    }

    @Override
    public void validateZoneLimit(DuAn duAn) {
        DangKyGoi dangKyGoi = getCurrentPackage(duAn);
        validatePackageStatus(duAn);

        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();
        int currentZones = duAn.getKhuVucs().size();

        if (currentZones >= goiCuoc.getSlKhuVucToiDa()) {
            throw new PackageLimitExceededException(
                    "Số lượng khu vực đã đạt giới hạn của gói cước (" +
                            goiCuoc.getSlKhuVucToiDa() + " khu vực)");
        }
    }

    @Override
    public void validateRuleLimit(DuAn duAn) {
        DangKyGoi dangKyGoi = getCurrentPackage(duAn);
        validatePackageStatus(duAn);

        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();
        int currentRules = 0; // This should be implemented based on your rule entity

        if (currentRules >= goiCuoc.getSlLuatToiDa()) {
            throw new PackageLimitExceededException(
                    "Số lượng luật đã đạt giới hạn của gói cước (" +
                            goiCuoc.getSlLuatToiDa() + " luật)");
        }
    }

    @Override
    public DangKyGoi getCurrentPackage(DuAn duAn) {
        if (duAn.getNguoiDung() == null) {
            throw new PackageExpiredException("Dự án chưa gán người dùng hợp lệ");
        }
        return duAn.getNguoiDung().getDangKyGois().stream()
                .filter(dkg -> "ACTIVE".equals(dkg.getTrangThai()))
                .findFirst()
                .orElseThrow(
                        () -> new PackageExpiredException("Người dùng chưa đăng ký gói cước hoặc gói cước đã hết hạn"));
    }

    @Override
    public boolean isPackageExpired(DangKyGoi dangKyGoi) {
        return dangKyGoi.getNgayKetThuc() != null &&
                dangKyGoi.getNgayKetThuc().isBefore(LocalDateTime.now());
    }

    @Override
    public DangKyGoi validatePackageStatus(DuAn duAn) {
        DangKyGoi dangKyGoi = getCurrentPackage(duAn);
        if (isPackageExpired(dangKyGoi)) {
            throw new PackageExpiredException(
                    "Gói cước đã hết hạn. Vui lòng gia hạn hoặc nâng cấp gói cước để tiếp tục sử dụng");
        }
        return dangKyGoi;
    }

}