package com.iot.management.controller.api.admin;

import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.repository.GoiCuocRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goi-cuoc")
@PreAuthorize("hasRole('ADMIN')")
public class GoiCuocAdminController {

    private final GoiCuocRepository goiCuocRepository;

    public GoiCuocAdminController(GoiCuocRepository goiCuocRepository) {
        this.goiCuocRepository = goiCuocRepository;
    }

    @GetMapping
    public ResponseEntity<List<GoiCuoc>> getAllPackages() {
        List<GoiCuoc> packages = goiCuocRepository.findAll();
        return ResponseEntity.ok(packages);
    }
}
