package com.iot.management.service;
import com.iot.management.model.dto.request.GoiCuocRequest;
import com.iot.management.model.entity.GoiCuoc;
import java.util.List;
import java.util.Optional;

public interface GoiCuocService {
    List<GoiCuoc> findAll();
    Optional<GoiCuoc> findById(Integer id);
    GoiCuoc save(GoiCuocRequest request);
    GoiCuoc update(Integer id, GoiCuocRequest request);
    void deleteById(Integer id);
}