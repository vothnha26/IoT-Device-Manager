package com.iot.management.service.impl;

import com.iot.management.exception.PackageExpiredException;
import com.iot.management.exception.PackageLimitExceededException;
import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.service.DangKyGoiService;
import com.iot.management.service.PackageLimitService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PackageLimitServiceImpl implements PackageLimitService {

    private final DangKyGoiService dangKyGoiService;

    public PackageLimitServiceImpl(DangKyGoiService dangKyGoiService) {
        this.dangKyGoiService = dangKyGoiService;
    }

    @Override
    public void validateDeviceLimit(DuAn duAn) {
        DangKyGoi dangKyGoi = validatePackageStatus(duAn); // ✅ Đổi chỗ và nhận luôn đối tượng
        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();

        if (goiCuoc == null) {
            throw new PackageExpiredException("Gói cước không hợp lệ");
        }

        int currentDevices = duAn.getKhuVucs().stream()
                .mapToInt(khuVuc -> khuVuc.getThietBis().size())
                .sum();
                
        if (currentDevices >= goiCuoc.getSlThietBiToiDa()) {
            throw new PackageLimitExceededException(
                "Số lượng thiết bị đã đạt giới hạn của gói cước (" + 
                goiCuoc.getSlThietBiToiDa() + " thiết bị)"
            );
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
                goiCuoc.getSlKhuVucToiDa() + " khu vực)"
            );
        }
    }

    @Override
    public void validateRuleLimit(DuAn duAn) {
        DangKyGoi dangKyGoi = getCurrentPackage(duAn);
        validatePackageStatus(duAn);
        
        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();
        // TODO: Implement rule counting logic
        int currentRules = 0; // This should be implemented based on your rule entity
        
        if (currentRules >= goiCuoc.getSlLuatToiDa()) {
            throw new PackageLimitExceededException(
                "Số lượng luật đã đạt giới hạn của gói cước (" + 
                goiCuoc.getSlLuatToiDa() + " luật)"
            );
        }
    }

    @Override
    public DangKyGoi getCurrentPackage(DuAn duAn) {
        if (duAn.getNguoiDung() == null) {
    throw new PackageExpiredException("Dự án chưa gán người dùng hợp lệ");
    }
        return duAn.getNguoiDung().getDangKyGois().stream()
                .filter(dkg -> "DA_THANH_TOAN".equals(dkg.getTrangThai()))
                .findFirst()
                .orElseThrow(() -> new PackageExpiredException("Người dùng chưa đăng ký gói cước hoặc gói cước đã hết hạn"));
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
            "Gói cước đã hết hạn. Vui lòng gia hạn hoặc nâng cấp gói cước để tiếp tục sử dụng"
        );
    }
    return dangKyGoi;
}

}