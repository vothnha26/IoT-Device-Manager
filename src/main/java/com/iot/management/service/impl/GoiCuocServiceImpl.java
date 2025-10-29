package com.iot.management.service.impl;

import com.iot.management.model.dto.request.GoiCuocRequest;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.repository.GoiCuocRepository;
import com.iot.management.service.GoiCuocService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoiCuocServiceImpl implements GoiCuocService {

    private final GoiCuocRepository goiCuocRepository;

    public GoiCuocServiceImpl(GoiCuocRepository goiCuocRepository) {
        this.goiCuocRepository = goiCuocRepository;
    }

    @Override
    public List<GoiCuoc> findAll() {
        return goiCuocRepository.findAll();
    }

    @Override
    public Optional<GoiCuoc> findById(Integer id) {
        return goiCuocRepository.findById(id);
    }

    @Override
    public GoiCuoc save(GoiCuocRequest request) {
        // Prevent creating duplicate package names
        if (request.getTenGoi() != null && goiCuocRepository.findByTenGoi(request.getTenGoi()).isPresent()) {
            throw new IllegalArgumentException("Gói cước đã tồn tại: " + request.getTenGoi());
        }

        GoiCuoc goiCuoc = new GoiCuoc();
        // Logic to map DTO to Entity
        goiCuoc.setTenGoi(request.getTenGoi());
        goiCuoc.setGiaTien(request.getGiaTien());
        goiCuoc.setSlThietBiToiDa(request.getSlThietBiToiDa());
        goiCuoc.setSlLuatToiDa(request.getSlLuatToiDa());
        goiCuoc.setSoNgayLuuDuLieu(request.getSoNgayLuuDuLieu());
        // map newly added fields
        if (request.getSlKhuVucToiDa() != null) {
            goiCuoc.setSlKhuVucToiDa(request.getSlKhuVucToiDa());
        } else {
            goiCuoc.setSlKhuVucToiDa(10); // default
        }
        if (request.getSlTokenToiDa() != null) {
            goiCuoc.setSlTokenToiDa(request.getSlTokenToiDa());
        } else {
            goiCuoc.setSlTokenToiDa(5); // default
        }
        return goiCuocRepository.save(goiCuoc);
    }

    @Override
    public GoiCuoc update(Integer id, GoiCuocRequest request) {
        GoiCuoc existingGoiCuoc = goiCuocRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy gói cước với ID: " + id));

        // If the name is changed, ensure no other package uses the same name
        if (request.getTenGoi() != null && !request.getTenGoi().equals(existingGoiCuoc.getTenGoi())) {
            Optional<GoiCuoc> byName = goiCuocRepository.findByTenGoi(request.getTenGoi());
            if (byName.isPresent() && !byName.get().getMaGoiCuoc().equals(id)) {
                throw new IllegalArgumentException("Gói cước khác đã sử dụng tên: " + request.getTenGoi());
            }
            existingGoiCuoc.setTenGoi(request.getTenGoi());
        } else if (request.getTenGoi() != null) {
            existingGoiCuoc.setTenGoi(request.getTenGoi());
        }
        existingGoiCuoc.setGiaTien(request.getGiaTien());
        existingGoiCuoc.setSlThietBiToiDa(request.getSlThietBiToiDa());
        existingGoiCuoc.setSlLuatToiDa(request.getSlLuatToiDa());
        existingGoiCuoc.setSoNgayLuuDuLieu(request.getSoNgayLuuDuLieu());
        // update newly added fields
        if (request.getSlKhuVucToiDa() != null) {
            existingGoiCuoc.setSlKhuVucToiDa(request.getSlKhuVucToiDa());
        }
        if (request.getSlTokenToiDa() != null) {
            existingGoiCuoc.setSlTokenToiDa(request.getSlTokenToiDa());
        }

        return goiCuocRepository.save(existingGoiCuoc);
    }

    @Override
    public void deleteById(Integer id) {
        if (!goiCuocRepository.existsById(id)) {
            throw new EntityNotFoundException("Không tìm thấy gói cước với ID: " + id);
        }
        goiCuocRepository.deleteById(id);
    }
}	