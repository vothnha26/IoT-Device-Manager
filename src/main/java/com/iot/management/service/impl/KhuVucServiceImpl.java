package com.iot.management.service.impl;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.service.KhuVucService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuVucServiceImpl implements KhuVucService {

    private final KhuVucRepository khuVucRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public KhuVucServiceImpl(KhuVucRepository khuVucRepository, NguoiDungRepository nguoiDungRepository) {
        this.khuVucRepository = khuVucRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public KhuVuc createLocation(Long ownerId, Long parentLocationId, KhuVuc khuVuc) {
        NguoiDung owner = nguoiDungRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + ownerId));
        khuVuc.setChuSoHuu(owner);

        if (parentLocationId != null) {
            KhuVuc parent = khuVucRepository.findById(parentLocationId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực cha với ID: " + parentLocationId));
            khuVuc.setKhuVucCha(parent);
        }

        return khuVucRepository.save(khuVuc);
    }

    @Override
    public List<KhuVuc> findRootLocationsByOwner(Long ownerId) {
        return khuVucRepository.findByChuSoHuu_MaNguoiDungAndKhuVucChaIsNull(ownerId);
    }

    @Override
    public List<KhuVuc> findChildLocations(Long parentLocationId) {
        return khuVucRepository.findByKhuVucCha_MaKhuVuc(parentLocationId);
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
        khuVuc.setKhuVucCha(existingLocation.getKhuVucCha());
        
        return khuVucRepository.save(khuVuc);
    }

    @Override
    public List<KhuVuc> getRootKhuVucsByUser(Long userId) {
        return khuVucRepository.findByChuSoHuu_MaNguoiDungAndKhuVucChaIsNull(userId);
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
        
        // Kiểm tra xem có khu vực con không
        List<KhuVuc> children = khuVucRepository.findByKhuVucCha_MaKhuVuc(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("Không thể xóa khu vực có khu vực con. Vui lòng xóa các khu vực con trước.");
        }
        
        khuVucRepository.delete(khuVuc);
    }
}