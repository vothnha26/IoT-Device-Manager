package com.iot.management.controller.api;

import com.iot.management.model.dto.DashboardStatsDTO;
import com.iot.management.service.DashboardService;
import com.iot.management.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        DashboardStatsDTO stats = dashboardService.getDashboardStats(currentUserId);
        return ResponseEntity.ok(stats);
    }
}
