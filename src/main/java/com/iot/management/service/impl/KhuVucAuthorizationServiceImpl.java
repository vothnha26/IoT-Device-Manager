package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.PhanQuyenDuAn;
import com.iot.management.model.entity.PhanQuyenKhuVuc;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.PhanQuyenDuAnRepository;
import com.iot.management.repository.PhanQuyenKhuVucRepository;
import com.iot.management.service.KhuVucAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KhuVucAuthorizationServiceImpl implements KhuVucAuthorizationService {

    @Autowired
    private PhanQuyenKhuVucRepository phanQuyenKhuVucRepository;

    @Autowired
    private PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public String layVaiTroTrongKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        Optional<PhanQuyenKhuVuc> phanQuyenOpt = phanQuyenKhuVucRepository
                .findByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung);
        
        if (phanQuyenOpt.isPresent()) {
            return phanQuyenOpt.get().getVaiTro();
        }
        
        return null;
    }

    @Override
    public boolean laQuanLyKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        String vaiTro = layVaiTroTrongKhuVuc(maKhuVuc, maNguoiDung);
        return "QUAN_LY_KHU_VUC".equals(vaiTro);
    }

    @Override
    public boolean coQuyenXemKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        // Có quyền nếu có bất kỳ vai trò nào trong khu vực
        String vaiTro = layVaiTroTrongKhuVuc(maKhuVuc, maNguoiDung);
        if (vaiTro != null) {
            return true;
        }
        
        // Hoặc có quyền quản lý thông qua dự án
        return coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    // ==================== QUYỀN XEM THÔNG TIN ====================

    @Override
    public boolean coQuyenXemThongTinKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        return coQuyenXemKhuVuc(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemDanhSachThietBi(Long maKhuVuc, Long maNguoiDung) {
        return coQuyenXemKhuVuc(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemDuLieuCamBien(Long maKhuVuc, Long maNguoiDung) {
        return coQuyenXemKhuVuc(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemCanhBao(Long maKhuVuc, Long maNguoiDung) {
        return coQuyenXemKhuVuc(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenXemNhatKy(Long maKhuVuc, Long maNguoiDung) {
        return coQuyenXemKhuVuc(maKhuVuc, maNguoiDung);
    }

    // ==================== QUYỀN CHỈNH SỬA ====================

    @Override
    public boolean coQuyenChinhSuaThongTinKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        // Chỉ QUAN_LY_KHU_VUC hoặc quản lý dự án mới có quyền
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    // ==================== QUYỀN QUẢN LÝ THIẾT BỊ ====================

    @Override
    public boolean coQuyenThemThietBi(Long maKhuVuc, Long maNguoiDung) {
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenXoaThietBi(Long maKhuVuc, Long maNguoiDung) {
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenDieuKhienThietBi(Long maKhuVuc, Long maNguoiDung) {
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    // ==================== QUYỀN PHÂN QUYỀN ====================

    @Override
    public boolean coQuyenCapQuyenThietBi(Long maKhuVuc, Long maNguoiDung) {
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    @Override
    public boolean coQuyenCapQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        return laQuanLyKhuVuc(maKhuVuc, maNguoiDung) || 
               coQuyenQuanLyThongQuaDuAn(maKhuVuc, maNguoiDung);
    }

    // ==================== KIỂM TRA QUYỀN ĐẶC BIỆT ====================

    @Override
    public boolean coQuyenQuanLyThongQuaDuAn(Long maKhuVuc, Long maNguoiDung) {
        // Lấy thông tin khu vực để biết nó thuộc dự án nào
        Optional<KhuVuc> khuVucOpt = khuVucRepository.findById(maKhuVuc);
        if (!khuVucOpt.isPresent()) {
            return false;
        }
        
        DuAn duAn = khuVucOpt.get().getDuAn();
        
        // Lấy thông tin người dùng
        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findById(maNguoiDung);
        if (!nguoiDungOpt.isPresent()) {
            return false;
        }
        
        // Kiểm tra vai trò trong dự án
        Optional<PhanQuyenDuAn> phanQuyenDuAnOpt = phanQuyenDuAnRepository
                .findByDuAnAndNguoiDung(duAn, nguoiDungOpt.get());
        
        if (!phanQuyenDuAnOpt.isPresent()) {
            return false;
        }
        
        DuAnRole vaiTro = phanQuyenDuAnOpt.get().getVaiTro();
        
        // CHU_SO_HUU hoặc QUAN_LY của dự án có quyền quản lý khu vực
        return vaiTro == DuAnRole.CHU_SO_HUU || vaiTro == DuAnRole.QUAN_LY;
    }
}
