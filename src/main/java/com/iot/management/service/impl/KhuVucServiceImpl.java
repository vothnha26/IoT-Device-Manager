package com.iot.management.service.impl;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.repository.DuAnRepository;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.ThietBiRepository;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.KhuVucAuthorizationService;
import com.iot.management.service.DuAnAuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuVucServiceImpl implements KhuVucService {

    private final KhuVucRepository khuVucRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DuAnRepository duAnRepository;

    @Autowired
    private KhuVucAuthorizationService khuVucAuthorizationService;

    @Autowired
    private DuAnAuthorizationService duAnAuthorizationService;

    @Autowired
    private ThietBiRepository thietBiRepository;

    public KhuVucServiceImpl(KhuVucRepository khuVucRepository,
            NguoiDungRepository nguoiDungRepository,
            DuAnRepository duAnRepository) {
        this.khuVucRepository = khuVucRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.duAnRepository = duAnRepository;
    }

    @Override
    public KhuVuc createLocation(Long ownerId, Long duAnId, KhuVuc khuVuc, String moTa) {
        NguoiDung owner = nguoiDungRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + ownerId));
        khuVuc.setChuSoHuu(owner);
        khuVuc.setMoTa(moTa);

        if (duAnId != null) {
            DuAn duAn = duAnRepository.findById(duAnId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án với ID: " + duAnId));
            khuVuc.setDuAn(duAn);
        }

        return khuVucRepository.save(khuVuc);
    }

    @Override
    public List<KhuVuc> findByDuAn(Long duAnId) {
        return khuVucRepository.findByDuAn_MaDuAn(duAnId);
    }

    @Override
    public List<KhuVuc> findKhuVucCoQuyenXem(Long duAnId, Long maNguoiDung) {
        // Lấy tất cả khu vực của dự án
        List<KhuVuc> allKhuVucs = khuVucRepository.findByDuAn_MaDuAn(duAnId);

        // Kiểm tra vai trò trong dự án
        DuAnRole role = duAnAuthorizationService.layVaiTroTrongDuAn(duAnId, maNguoiDung);

        // CHU_SO_HUU và QUAN_LY thấy tất cả khu vực
        if (role == DuAnRole.CHU_SO_HUU || role == DuAnRole.QUAN_LY) {
            return allKhuVucs;
        }

        // NGUOI_DUNG chỉ thấy khu vực được phân quyền cụ thể
        return allKhuVucs.stream()
                .filter(khuVuc -> khuVucAuthorizationService.coQuyenXemKhuVuc(khuVuc.getMaKhuVuc(), maNguoiDung))
                .collect(Collectors.toList());
    }

    @Override
    public KhuVuc updateLocation(Long ownerId, KhuVuc khuVuc) {
        // Kiểm tra xem khu vực có tồn tại không
        KhuVuc existingLocation = khuVucRepository.findById(khuVuc.getMaKhuVuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực với ID: " + khuVuc.getMaKhuVuc()));

        // Kiểm tra quyền sở hữu
        if (!existingLocation.getChuSoHuu().getMaNguoiDung().equals(ownerId)) {
            throw new RuntimeException("Bạn không có quyền cập nhật khu vực này");
        }

        // Giữ nguyên thông tin chủ sở hữu và khu vực cha
        khuVuc.setChuSoHuu(existingLocation.getChuSoHuu());

        return khuVucRepository.save(khuVuc);
    }

    @Override
    public List<KhuVuc> getAllKhuVucsByUser(Long userId) {
        return khuVucRepository.findByChuSoHuu_MaNguoiDung(userId);
    }

    @Override
    public KhuVuc getKhuVucById(Long id) {
        return khuVucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực với ID: " + id));
    }

    @Override
    @Transactional
    public void deleteKhuVuc(Long id) {
        KhuVuc khuVuc = getKhuVucById(id);

        // Check if area has devices
        long deviceCount = thietBiRepository.countByKhuVuc_MaKhuVuc(id);
        if (deviceCount > 0) {
            throw new RuntimeException("Không thể xóa khu vực vì còn " + deviceCount
                    + " thiết bị. Vui lòng xóa hoặc di chuyển thiết bị trước.");
        }

        // Delete area (cascade will automatically delete phan_quyen_khu_vuc records)
        khuVucRepository.delete(khuVuc);
    }
}