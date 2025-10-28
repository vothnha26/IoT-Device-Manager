package com.iot.management.controller.api.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.LoaiThietBiRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.model.repository.ThongBaoRepository;

@RestController
@RequestMapping("/api/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private DuAnRepository duAnRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get all counts
        stats.put("totalUsers", nguoiDungRepository.count());
        stats.put("totalDevices", thietBiRepository.count());
        stats.put("totalDeviceTypes", loaiThietBiRepository.count());
        stats.put("totalAreas", khuVucRepository.count());
        stats.put("totalProjects", duAnRepository.count());

        // Get today's notifications count
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long todayNotifications = thongBaoRepository.countByThoiGianTaoBetween(startOfDay, endOfDay);
        stats.put("todayNotifications", todayNotifications);

        return ResponseEntity.ok(stats);
    }
}
