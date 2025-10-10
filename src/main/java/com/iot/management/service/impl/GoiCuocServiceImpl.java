package com.iot.management.service.impl;

import com.iot.management.model.dto.request.GoiCuocRequest;
import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.repository.GoiCuocRepository;
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
        GoiCuoc goiCuoc = new GoiCuoc();
        // Logic to map DTO to Entity
        goiCuoc.setTenGoi(request.getTenGoi());
        goiCuoc.setGiaTien(request.getGiaTien());
        goiCuoc.setSlThietBiToiDa(request.getSlThietBiToiDa());
        goiCuoc.setSlLuatToiDa(request.getSlLuatToiDa());
        goiCuoc.setSoNgayLuuDuLieu(request.getSoNgayLuuDuLieu());
        return goiCuocRepository.save(goiCuoc);
    }

    @Override
    public GoiCuoc update(Integer id, GoiCuocRequest request) {
        GoiCuoc existingGoiCuoc = goiCuocRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy gói cước với ID: " + id));

        existingGoiCuoc.setTenGoi(request.getTenGoi());
        existingGoiCuoc.setGiaTien(request.getGiaTien());
        existingGoiCuoc.setSlThietBiToiDa(request.getSlThietBiToiDa());
        existingGoiCuoc.setSlLuatToiDa(request.getSlLuatToiDa());
        existingGoiCuoc.setSoNgayLuuDuLieu(request.getSoNgayLuuDuLieu());

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