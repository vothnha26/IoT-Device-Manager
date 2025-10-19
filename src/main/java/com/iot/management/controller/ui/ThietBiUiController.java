package com.iot.management.controller.ui;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.LoaiThietBiService;
import com.iot.management.service.ThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller("thietBiUiController")
@RequestMapping("/thiet-bi")
public class ThietBiUiController {

    @Autowired
    private ThietBiService thietBiService;
    
    @Autowired
    private LoaiThietBiService loaiThietBiService;
    
    @Autowired
    private KhuVucService khuVucService;

    @GetMapping
    public String thietBiManagement(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set thông tin cơ bản
        model.addAttribute("title", "Quản lý Thiết bị - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        
        Long userId = currentUser.getMaNguoiDung();
        
        // Lấy danh sách thiết bị của user
        List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
        model.addAttribute("devices", devices);
        
        // Lấy danh sách loại thiết bị cho dropdown
        List<LoaiThietBi> deviceTypes = loaiThietBiService.findAllDeviceTypes();
        model.addAttribute("deviceTypes", deviceTypes);
        
        // Lấy danh sách khu vực cho dropdown
        List<KhuVuc> locations = khuVucService.getAllKhuVucsByUser(userId);
        model.addAttribute("locations", locations);
        
        // Thống kê
        long totalDevices = devices.size();
        long activeDevices = devices.stream()
            .filter(d -> "hoat dong".equalsIgnoreCase(d.getTrangThai()))
            .count();
        long inactiveDevices = totalDevices - activeDevices;
        
        model.addAttribute("totalDevices", totalDevices);
        model.addAttribute("activeDevices", activeDevices);
        model.addAttribute("inactiveDevices", inactiveDevices);
        
        return "thiet-bi/index";
    }
}
