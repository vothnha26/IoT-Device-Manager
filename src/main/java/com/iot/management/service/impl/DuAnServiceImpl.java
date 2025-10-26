package com.iot.management.service.impl;

import com.iot.management.model.dto.request.DuAnRequest;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.PhanQuyenDuAn;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.PhanQuyenDuAnRepository;
import java.util.stream.Collectors;
import java.util.HashSet;
import com.iot.management.service.DuAnService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DuAnServiceImpl implements DuAnService {

    private final DuAnRepository duAnRepository;
    private final PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    public DuAnServiceImpl(DuAnRepository duAnRepository, 
                          PhanQuyenDuAnRepository phanQuyenDuAnRepository) {
        this.duAnRepository = duAnRepository;
        this.phanQuyenDuAnRepository = phanQuyenDuAnRepository;
    }

    @Override
    @Transactional
    public DuAn create(DuAnRequest request, NguoiDung nguoiDung) {
        // Kiểm tra xem tên dự án đã tồn tại chưa
        if (existsByTenDuAnAndNguoiDung(request.getTenDuAn(), nguoiDung)) {
            throw new IllegalArgumentException("Tên dự án đã tồn tại");
        }
        
        // Tạo dự án mới
        DuAn newDuAn = new DuAn();
        newDuAn.setTenDuAn(request.getTenDuAn());
        newDuAn.setMoTa(request.getMoTa());
        newDuAn.setDiaChi(request.getDiaChi());
        newDuAn.setNguoiDung(nguoiDung);
        newDuAn.setNgayTao(LocalDateTime.now());
        newDuAn.setTrangThai("HOAT_DONG");
        DuAn savedDuAn = duAnRepository.save(newDuAn);

        // Tạo quyền chủ sở hữu cho người tạo
        PhanQuyenDuAn phanQuyen = new PhanQuyenDuAn();
        phanQuyen.setDuAn(savedDuAn);
        phanQuyen.setNguoiDung(nguoiDung);
        phanQuyen.setVaiTro(DuAnRole.CHU_SO_HUU);
        phanQuyen.setNgayCapQuyen(LocalDateTime.now());
        phanQuyenDuAnRepository.save(phanQuyen);
        
        return savedDuAn;
    }

    @Override
    @Transactional
    public DuAn update(Long maDuAn, DuAnRequest request, NguoiDung nguoiDung) {
        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dự án"));

        // Kiểm tra quyền sở hữu
        if (!duAn.getNguoiDung().equals(nguoiDung)) {
            throw new AccessDeniedException("Không có quyền chỉnh sửa dự án này");
        }

        // Kiểm tra xem tên mới có trùng với dự án khác không
        if (!duAn.getTenDuAn().equals(request.getTenDuAn()) && 
            existsByTenDuAnAndNguoiDung(request.getTenDuAn(), nguoiDung)) {
            throw new IllegalArgumentException("Tên dự án đã tồn tại");
        }

        duAn.setTenDuAn(request.getTenDuAn());
        duAn.setMoTa(request.getMoTa());
        duAn.setDiaChi(request.getDiaChi());

        return duAnRepository.save(duAn);
    }

    @Override
    @Transactional
    public void delete(Long maDuAn, NguoiDung nguoiDung) {
        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy dự án"));

        if (!duAn.getNguoiDung().equals(nguoiDung)) {
            throw new AccessDeniedException("Không có quyền xóa dự án này");
        }

        duAnRepository.delete(duAn);
    }

    @Override
    public List<DuAn> findAllByNguoiDung(NguoiDung nguoiDung) {
        return phanQuyenDuAnRepository.findByNguoiDung(nguoiDung)
            .stream()
            .map(PhanQuyenDuAn::getDuAn)
            .filter(duAn -> "HOAT_DONG".equals(duAn.getTrangThai()))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<DuAn> findByIdAndNguoiDung(Long maDuAn, NguoiDung nguoiDung) {
        return duAnRepository.findByMaDuAnAndNguoiDungAndTrangThai(maDuAn, nguoiDung, "HOAT_DONG");
    }

    @Override
    public boolean existsByTenDuAnAndNguoiDung(String tenDuAn, NguoiDung nguoiDung) {
        return duAnRepository.existsByTenDuAnAndNguoiDung(tenDuAn, nguoiDung);
    }
}