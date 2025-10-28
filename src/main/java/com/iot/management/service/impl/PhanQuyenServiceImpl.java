package com.iot.management.service.impl;

import com.iot.management.model.entity.*;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.model.repository.*;
import com.iot.management.service.PhanQuyenService;
import com.iot.management.dto.AreaPermissionDTO;
import com.iot.management.dto.DevicePermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PhanQuyenServiceImpl implements PhanQuyenService {

    @Autowired
    private PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    @Autowired
    private PhanQuyenThietBiRepository phanQuyenThietBiRepository;

    @Autowired
    private QuyenHeThongRepository quyenHeThongRepository;

    @Autowired
    private VaiTroQuyenRepository vaiTroQuyenRepository;

    @Autowired
    private DuAnRepository duAnRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Autowired
    private PhanQuyenKhuVucRepository phanQuyenKhuVucRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    // ==================== Phân quyền dự án ====================

    @Override
    public PhanQuyenDuAn capQuyenDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTro) {
        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án với ID: " + maDuAn));

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + maNguoiDung));

        // Kiểm tra xem đã có quyền chưa
        Optional<PhanQuyenDuAn> existing = phanQuyenDuAnRepository
                .findByDuAnAndNguoiDung(duAn, nguoiDung);

        if (existing.isPresent()) {
            // Cập nhật vai trò nếu đã tồn tại
            PhanQuyenDuAn phanQuyen = existing.get();
            phanQuyen.setVaiTro(vaiTro);
            return phanQuyenDuAnRepository.save(phanQuyen);
        }

        // Tạo mới nếu chưa tồn tại
        PhanQuyenDuAn phanQuyen = new PhanQuyenDuAn();
        phanQuyen.setDuAn(duAn);
        phanQuyen.setNguoiDung(nguoiDung);
        phanQuyen.setVaiTro(vaiTro);
        phanQuyen.setNgayCapQuyen(LocalDateTime.now());

        return phanQuyenDuAnRepository.save(phanQuyen);
    }

    @Override
    public void thuHoiQuyenDuAn(Long maDuAn, Long maNguoiDung) {
        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án với ID: " + maDuAn));

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + maNguoiDung));

        phanQuyenDuAnRepository.findByDuAnAndNguoiDung(duAn, nguoiDung)
                .ifPresent(phanQuyenDuAnRepository::delete);
    }

    @Override
    public PhanQuyenDuAn capNhatVaiTroDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTroMoi) {
        return capQuyenDuAn(maDuAn, maNguoiDung, vaiTroMoi);
    }

    @Override
    public List<PhanQuyenDuAn> layDanhSachQuyenDuAn(Long maDuAn) {
        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án với ID: " + maDuAn));

        return phanQuyenDuAnRepository.findByDuAn(duAn);
    }

    @Override
    public boolean kiemTraQuyenDuAn(Long maDuAn, Long maNguoiDung, DuAnRole vaiTro) {
        DuAn duAn = duAnRepository.findById(maDuAn).orElse(null);
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElse(null);

        if (duAn == null || nguoiDung == null) {
            return false;
        }

        Optional<PhanQuyenDuAn> phanQuyen = phanQuyenDuAnRepository
                .findByDuAnAndNguoiDung(duAn, nguoiDung);

        return phanQuyen.isPresent() && phanQuyen.get().getVaiTro() == vaiTro;
    }

    @Override
    public DuAnRole layVaiTroDuAn(Long maDuAn, Long maNguoiDung) {
        DuAn duAn = duAnRepository.findById(maDuAn).orElse(null);
        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung).orElse(null);

        if (duAn == null || nguoiDung == null) {
            return null;
        }

        return phanQuyenDuAnRepository.findByDuAnAndNguoiDung(duAn, nguoiDung)
                .map(PhanQuyenDuAn::getVaiTro)
                .orElse(null);
    }

    // ==================== Phân quyền thiết bị ====================

    @Override
    public PhanQuyenThietBi capQuyenThietBi(Long maThietBi, Long maNguoiDung, DuAnRole vaiTro,
                                             boolean coQuyenDieuKhien, boolean coQuyenXemDuLieu,
                                             boolean coQuyenChinhSua) {
        ThietBi thietBi = thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị với ID: " + maThietBi));

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + maNguoiDung));

        // Kiểm tra xem đã có quyền chưa
        Optional<PhanQuyenThietBi> existing = phanQuyenThietBiRepository
                .findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung);

        if (existing.isPresent()) {
            // Cập nhật quyền nếu đã tồn tại
            PhanQuyenThietBi phanQuyen = existing.get();
            phanQuyen.setVaiTro(vaiTro);
            phanQuyen.setCoQuyenDieuKhien(coQuyenDieuKhien);
            phanQuyen.setCoQuyenXemDuLieu(coQuyenXemDuLieu);
            phanQuyen.setCoQuyenChinhSua(coQuyenChinhSua);
            return phanQuyenThietBiRepository.save(phanQuyen);
        }

        // Tạo mới nếu chưa tồn tại
        PhanQuyenThietBi phanQuyen = new PhanQuyenThietBi();
        phanQuyen.setThietBi(thietBi);
        phanQuyen.setNguoiDung(nguoiDung);
        phanQuyen.setVaiTro(vaiTro);
        phanQuyen.setCoQuyenDieuKhien(coQuyenDieuKhien);
        phanQuyen.setCoQuyenXemDuLieu(coQuyenXemDuLieu);
        phanQuyen.setCoQuyenChinhSua(coQuyenChinhSua);
        phanQuyen.setNgayCapQuyen(LocalDateTime.now());

        return phanQuyenThietBiRepository.save(phanQuyen);
    }

    @Override
    public void thuHoiQuyenThietBi(Long maThietBi, Long maNguoiDung) {
        phanQuyenThietBiRepository.deleteByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung);
    }

    @Override
    public PhanQuyenThietBi capNhatQuyenThietBi(Long maThietBi, Long maNguoiDung,
                                                 boolean coQuyenDieuKhien, boolean coQuyenXemDuLieu,
                                                 boolean coQuyenChinhSua) {
        PhanQuyenThietBi phanQuyen = phanQuyenThietBiRepository
                .findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phân quyền thiết bị"));

        phanQuyen.setCoQuyenDieuKhien(coQuyenDieuKhien);
        phanQuyen.setCoQuyenXemDuLieu(coQuyenXemDuLieu);
        phanQuyen.setCoQuyenChinhSua(coQuyenChinhSua);

        return phanQuyenThietBiRepository.save(phanQuyen);
    }

    @Override
    public List<PhanQuyenThietBi> layDanhSachQuyenThietBi(Long maThietBi) {
        return phanQuyenThietBiRepository.findByMaThietBi(maThietBi);
    }

    @Override
    public boolean kiemTraQuyenDieuKhienThietBi(Long maThietBi, Long maNguoiDung) {
        return phanQuyenThietBiRepository.findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung)
                .map(PhanQuyenThietBi::getCoQuyenDieuKhien)
                .orElse(false);
    }

    @Override
    public boolean kiemTraQuyenXemDuLieuThietBi(Long maThietBi, Long maNguoiDung) {
        return phanQuyenThietBiRepository.findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung)
                .map(PhanQuyenThietBi::getCoQuyenXemDuLieu)
                .orElse(false);
    }

    @Override
    public boolean kiemTraQuyenChinhSuaThietBi(Long maThietBi, Long maNguoiDung) {
        return phanQuyenThietBiRepository.findByMaThietBiAndMaNguoiDung(maThietBi, maNguoiDung)
                .map(PhanQuyenThietBi::getCoQuyenChinhSua)
                .orElse(false);
    }

    // ==================== Quản lý quyền hệ thống ====================

    @Override
    public QuyenHeThong taoQuyenHeThong(String maNhom, String tenQuyen, String moTa) {
        if (quyenHeThongRepository.existsByTenQuyen(tenQuyen)) {
            throw new RuntimeException("Quyền hệ thống đã tồn tại: " + tenQuyen);
        }

        QuyenHeThong quyen = new QuyenHeThong(maNhom, tenQuyen, moTa);
        return quyenHeThongRepository.save(quyen);
    }

    @Override
    public List<QuyenHeThong> layDanhSachQuyenHeThong() {
        return quyenHeThongRepository.findAll();
    }

    @Override
    public List<QuyenHeThong> layQuyenTheoNhom(String maNhom) {
        return quyenHeThongRepository.findByMaNhom(maNhom);
    }

    @Override
    public VaiTroQuyen capQuyenChoVaiTro(Long maVaiTro, Long maQuyen) {
        // Validate vai trò tồn tại
        vaiTroRepository.findById(maVaiTro)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với ID: " + maVaiTro));

        // Validate quyền tồn tại
        quyenHeThongRepository.findById(maQuyen)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền với ID: " + maQuyen));

        VaiTroQuyen vaiTroQuyen = new VaiTroQuyen(maVaiTro, maQuyen);
        return vaiTroQuyenRepository.save(vaiTroQuyen);
    }

    @Override
    public void thuHoiQuyenTuVaiTro(Long maVaiTro, Long maQuyen) {
        VaiTroQuyen.VaiTroQuyenId id = new VaiTroQuyen.VaiTroQuyenId(maVaiTro, maQuyen);
        vaiTroQuyenRepository.deleteById(id);
    }

    @Override
    public List<QuyenHeThong> layQuyenCuaVaiTro(Long maVaiTro) {
        List<VaiTroQuyen> vaiTroQuyens = vaiTroQuyenRepository.findByMaVaiTro(maVaiTro);
        return vaiTroQuyens.stream()
                .map(VaiTroQuyen::getQuyenHeThong)
                .collect(Collectors.toList());
    }

    @Override
    public boolean kiemTraVaiTroCoQuyen(Long maVaiTro, String tenQuyen) {
        List<QuyenHeThong> quyens = layQuyenCuaVaiTro(maVaiTro);
        return quyens.stream()
                .anyMatch(q -> q.getTenQuyen().equals(tenQuyen));
    }
    
    // ==================== Phân quyền khu vực ====================

    @Override
    public PhanQuyenKhuVuc capQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung, String vaiTro) {
        KhuVuc khuVuc = khuVucRepository.findById(maKhuVuc)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực với ID: " + maKhuVuc));

        NguoiDung nguoiDung = nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + maNguoiDung));

        // Kiểm tra xem đã có quyền chưa
        Optional<PhanQuyenKhuVuc> existing = phanQuyenKhuVucRepository
                .findByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung);

        if (existing.isPresent()) {
            // Cập nhật vai trò nếu đã tồn tại
            PhanQuyenKhuVuc phanQuyen = existing.get();
            phanQuyen.setVaiTro(vaiTro);
            return phanQuyenKhuVucRepository.save(phanQuyen);
        }

        // Tạo mới nếu chưa tồn tại
        PhanQuyenKhuVuc phanQuyen = new PhanQuyenKhuVuc();
        phanQuyen.setKhuVuc(khuVuc);
        phanQuyen.setNguoiDung(nguoiDung);
        phanQuyen.setVaiTro(vaiTro);
        phanQuyen.setNgayCapQuyen(LocalDateTime.now());

        return phanQuyenKhuVucRepository.save(phanQuyen);
    }

    @Override
    public void thuHoiQuyenKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        phanQuyenKhuVucRepository.deleteByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung);
    }

    @Override
    public PhanQuyenKhuVuc capNhatVaiTroKhuVuc(Long maKhuVuc, Long maNguoiDung, String vaiTroMoi) {
        return capQuyenKhuVuc(maKhuVuc, maNguoiDung, vaiTroMoi);
    }

    @Override
    public List<PhanQuyenKhuVuc> layDanhSachQuyenKhuVuc(Long maKhuVuc) {
        return phanQuyenKhuVucRepository.findByMaKhuVuc(maKhuVuc);
    }

    @Override
    public boolean kiemTraQuyenQuanLyKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        return phanQuyenKhuVucRepository.findByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung)
                .map(pq -> "QUAN_LY_KHU_VUC".equals(pq.getVaiTro()))
                .orElse(false);
    }

    @Override
    public boolean kiemTraQuyenXemKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        return phanQuyenKhuVucRepository.findByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung)
                .map(pq -> "XEM".equals(pq.getVaiTro()) || "QUAN_LY_KHU_VUC".equals(pq.getVaiTro()))
                .orElse(false);
    }

    @Override
    public String layVaiTroKhuVuc(Long maKhuVuc, Long maNguoiDung) {
        return phanQuyenKhuVucRepository.findByMaKhuVucAndMaNguoiDung(maKhuVuc, maNguoiDung)
                .map(PhanQuyenKhuVuc::getVaiTro)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public void capNhatPhanQuyenChiTiet(
            Long userId, 
            Long duAnId, 
            NguoiDung targetUser,
            List<AreaPermissionDTO> areaPermissions,
            List<DevicePermissionDTO> devicePermissions) {
        
        // Lấy entity DuAn
        DuAn duAn = duAnRepository.findById(duAnId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));
        
        // Lấy vai trò dự án của target user (người được cấp quyền)
        PhanQuyenDuAn phanQuyenDuAn = phanQuyenDuAnRepository
                .findByDuAnAndNguoiDung(duAn, targetUser)
                .orElseThrow(() -> new RuntimeException("Người dùng không có quyền trong dự án này"));
        
        DuAnRole targetUserRole = phanQuyenDuAn.getVaiTro();
        
        // Xóa tất cả phân quyền hiện tại của target user
        List<PhanQuyenKhuVuc> existingAreaPerms = phanQuyenKhuVucRepository.findByMaNguoiDungAndMaDuAn(targetUser.getMaNguoiDung(), duAnId);
        if (!existingAreaPerms.isEmpty()) {
            phanQuyenKhuVucRepository.deleteAll(existingAreaPerms);
            phanQuyenKhuVucRepository.flush();
        }
        
        List<PhanQuyenThietBi> existingDevicePerms = phanQuyenThietBiRepository.findByMaNguoiDungAndMaDuAn(targetUser.getMaNguoiDung(), duAnId);
        if (!existingDevicePerms.isEmpty()) {
            phanQuyenThietBiRepository.deleteAll(existingDevicePerms);
            phanQuyenThietBiRepository.flush();
        }
        
        // Thêm phân quyền khu vực mới
        if (areaPermissions != null && !areaPermissions.isEmpty()) {
            for (AreaPermissionDTO areaDto : areaPermissions) {
                KhuVuc khuVuc = khuVucRepository.findById(areaDto.getMaKhuVuc())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực ID: " + areaDto.getMaKhuVuc()));
                
                PhanQuyenKhuVuc phanQuyen = new PhanQuyenKhuVuc();
                phanQuyen.setKhuVuc(khuVuc);
                phanQuyen.setNguoiDung(targetUser);
                phanQuyen.setVaiTro(areaDto.getVaiTro());
                phanQuyen.setNgayCapQuyen(LocalDateTime.now());
                
                phanQuyenKhuVucRepository.save(phanQuyen);
            }
        }
        
        // Thêm phân quyền thiết bị mới
        if (devicePermissions != null && !devicePermissions.isEmpty()) {
            for (DevicePermissionDTO deviceDto : devicePermissions) {
                ThietBi thietBi = thietBiRepository.findById(deviceDto.getMaThietBi())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị ID: " + deviceDto.getMaThietBi()));
                
                PhanQuyenThietBi phanQuyen = new PhanQuyenThietBi();
                phanQuyen.setThietBi(thietBi);
                phanQuyen.setNguoiDung(targetUser);
                phanQuyen.setVaiTro(targetUserRole); // Sử dụng vai trò dự án của target user
                phanQuyen.setCoQuyenXemDuLieu(deviceDto.isCoQuyenXemDuLieu());
                phanQuyen.setCoQuyenDieuKhien(deviceDto.isCoQuyenDieuKhien());
                phanQuyen.setNgayCapQuyen(LocalDateTime.now());
                
                phanQuyenThietBiRepository.save(phanQuyen);
            }
        }
    }
}
