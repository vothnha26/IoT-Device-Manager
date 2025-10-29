package com.iot.management.controller.ui;

import com.iot.management.model.dto.DashboardStatsDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Dashboard - IoT Management");
        model.addAttribute("currentUri", request.getRequestURI());

        // Tạo dữ liệu mẫu cho dashboard
        DashboardStatsDTO stats = new DashboardStatsDTO();
        stats.setTotalKhuVuc(5L);
        stats.setTotalThietBi(12L);
        stats.setTotalControllers(7L);
        stats.setTotalSensors(3L);
        stats.setTotalActuators(2L);
        stats.setDevicesOnline(9L);
        stats.setDevicesOffline(3L);

        // Thêm vào model
        model.addAttribute("stats", stats);

        return "dashboard";
    }

    @GetMapping("/dashboard/khu-vuc")
    public String areas(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Quản lý khu vực - IoT Management");
        model.addAttribute("currentUri", request.getRequestURI());
        return "khu-vuc/index";
    }

    @GetMapping("/dashboard/thiet-bi")
    public String devices(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Quản lý thiết bị - IoT Management");
        model.addAttribute("currentUri", request.getRequestURI());
        return "thiet-bi/index";
    }

    @GetMapping("dashboard/thong-ke")
    public String statistics(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Thống kê - IoT Management");
        model.addAttribute("currentUri", request.getRequestURI());
        return "thong-ke/index";
    }

    @GetMapping("/dashboard/du-lieu-cam-bien")
    public String sensorData(Model model) {
        model.addAttribute("title", "Dữ liệu cảm biến - IoT Management");
        return "du-lieu-cam-bien/index";
    }
}
