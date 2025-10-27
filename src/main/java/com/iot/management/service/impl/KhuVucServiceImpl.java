package com.iot.management.service.impl;

import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.service.KhuVucService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuVucServiceImpl implements KhuVucService {

    private final KhuVucRepository khuVucRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DuAnRepository duAnRepository;

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
    public void deleteKhuVuc(Long id) {
        KhuVuc khuVuc = getKhuVucById(id);
        khuVucRepository.delete(khuVuc);
    }
}