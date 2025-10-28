package com.iot.management.controller.api.user;

import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.repository.GoiCuocRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class GoiCuocController {

    private final GoiCuocRepository goiCuocRepository;

    public GoiCuocController(GoiCuocRepository goiCuocRepository) {
        this.goiCuocRepository = goiCuocRepository;
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<GoiCuoc>> getRecommendedPackages() {
        List<GoiCuoc> packages = goiCuocRepository.findAll();
        return ResponseEntity.ok(packages);
    }
    
    @GetMapping
    public ResponseEntity<List<GoiCuoc>> getAllPackages() {
        List<GoiCuoc> packages = goiCuocRepository.findAll();
        return ResponseEntity.ok(packages);
    }
}