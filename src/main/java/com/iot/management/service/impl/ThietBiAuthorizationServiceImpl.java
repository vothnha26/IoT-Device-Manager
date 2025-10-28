package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.PhanQuyenThietBi;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.PhanQuyenThietBiRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.DuAnAuthorizationService;
import com.iot.management.service.KhuVucAuthorizationService;
import com.iot.management.service.ThietBiAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThietBiAuthorizationServiceImpl implements ThietBiAuthorizationService {

    @Autowired
    private PhanQuyenThietBiRepository phanQuyenThietBiRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private KhuVucAuthorizationService khuVucAuthorizationService;

    @Autowired
    private DuAnAuthorizationService duAnAuthorizationService;

    @Override
    public String layQuyenThietBi(Long maThietBi, Long maNguoiDung) {
        // Kiểm tra quyền trực tiếp trên thiết bị
        Optional<PhanQuyenThietBi> phanQuyenOpt = phanQuyenThietBiRepository
                .findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung);
        
        if (phanQuyenOpt.isPresent()) {
            PhanQuyenThietBi phanQuyen = phanQuyenOpt.get();
            
            // Dựa vào các boolean flags để xác định loại quyền
            // Ưu tiên cao nhất: MANAGE > CONTROL > VIEW
            if (Boolean.TRUE.equals(phanQuyen.getCoQuyenChinhSua())) {
                return "MANAGE";
            } else if (Boolean.TRUE.equals(phanQuyen.getCoQuyenDieuKhien())) {
                // Có quyền điều khiển = có quyền xem + điều khiển
                return "CONTROL";
            } else if (Boolean.TRUE.equals(phanQuyen.getCoQuyenXemDuLieu())) {
                // Chỉ có quyền xem
                return "VIEW";
            }
        }
        
        // Kiểm tra quyền thông qua khu vực
        if (coQuyenQuanLyThongQuaKhuVuc(maThietBi, maNguoiDung)) {
            return "MANAGE";
        }
        
        // Kiểm tra quyền thông qua dự án
        if (coQuyenQuanLyThongQuaDuAn(maThietBi, maNguoiDung)) {
            return "MANAGE";
        }
        
        return null;
    }

    @Override
    public boolean coQuyenTruyCapThietBi(Long maThietBi, Long maNguoiDung) {
        return layQuyenThietBi(maThietBi, maNguoiDung) != null;
    }

    // ==================== QUYỀN VIEW ====================

    @Override
    public boolean coQuyenXemThongTin(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return quyen != null; // VIEW, CONTROL, MANAGE đều có quyền xem
    }

    @Override
    public boolean coQuyenXemDuLieu(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return quyen != null; // VIEW, CONTROL, MANAGE đều có quyền xem dữ liệu
    }

    @Override
    public boolean coQuyenXemLichSu(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return quyen != null; // VIEW, CONTROL, MANAGE đều có quyền xem lịch sử
    }

    // ==================== QUYỀN CONTROL ====================

    @Override
    public boolean coQuyenDieuKhien(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "CONTROL".equals(quyen) || "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenGuiLenh(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "CONTROL".equals(quyen) || "MANAGE".equals(quyen);
    }

    // ==================== QUYỀN MANAGE ====================

    @Override
    public boolean coQuyenChinhSuaThongTin(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenCauHinh(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenXoa(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenChiaSe(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenXemDanhSachNguoiDung(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenQuanLyLuat(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    @Override
    public boolean coQuyenQuanLyLichTrinh(Long maThietBi, Long maNguoiDung) {
        String quyen = layQuyenThietBi(maThietBi, maNguoiDung);
        return "MANAGE".equals(quyen);
    }

    // ==================== QUYỀN ĐẶC BIỆT ====================

    @Override
    public boolean coQuyenQuanLyThongQuaKhuVuc(Long maThietBi, Long maNguoiDung) {
        // Lấy thông tin thiết bị để biết nó thuộc khu vực nào
        Optional<ThietBi> thietBiOpt = thietBiRepository.findById(maThietBi);
        if (!thietBiOpt.isPresent()) {
            return false;
        }
        
        KhuVuc khuVuc = thietBiOpt.get().getKhuVuc();
        if (khuVuc == null) {
            return false;
        }
        
        // Kiểm tra quyền quản lý khu vực
        return khuVucAuthorizationService.laQuanLyKhuVuc(khuVuc.getMaKhuVuc(), maNguoiDung) ||
               khuVucAuthorizationService.coQuyenQuanLyThongQuaDuAn(khuVuc.getMaKhuVuc(), maNguoiDung);
    }

    @Override
    public boolean coQuyenQuanLyThongQuaDuAn(Long maThietBi, Long maNguoiDung) {
        // Lấy thông tin thiết bị
        Optional<ThietBi> thietBiOpt = thietBiRepository.findById(maThietBi);
        if (!thietBiOpt.isPresent()) {
            return false;
        }
        
        KhuVuc khuVuc = thietBiOpt.get().getKhuVuc();
        if (khuVuc == null) {
            return false;
        }
        
        Long maDuAn = khuVuc.getDuAn().getMaDuAn();
        
        // Kiểm tra quyền trong dự án (CHU_SO_HUU hoặc QUAN_LY)
        return duAnAuthorizationService.laChuSoHuu(maDuAn, maNguoiDung) ||
               duAnAuthorizationService.laQuanLyTroLen(maDuAn, maNguoiDung);
    }
}
