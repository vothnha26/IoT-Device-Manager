package com.iot.management.controller.ui;

import com.iot.management.model.dto.DashboardStatsDTO;
import com.iot.management.model.dto.RoomDTO;
import com.iot.management.security.SecurityUser;
import com.iot.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal SecurityUser currentUser) {
        // Nếu chưa login thì chuyển đến trang login
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        // Nếu đã login thì chuyển đến dashboard
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        
        // Nếu chưa login thì chuyển đến trang login
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        // Set title
        model.addAttribute("title", "Bảng điều khiển - IoT Manager");
        
        // Lấy thông tin user hiện tại
        Long userId = currentUser.getMaNguoiDung();
        String username = currentUser.getUsername();
        
        // Pass user info
        model.addAttribute("username", username);
        model.addAttribute("userId", userId);
        
        // Lấy thống kê dashboard
        DashboardStatsDTO stats = dashboardService.getDashboardStats(userId);
        model.addAttribute("stats", stats);
        
        // Lấy danh sách phòng với thiết bị
        List<RoomDTO> rooms = dashboardService.getRoomsWithDevices(userId);
        model.addAttribute("rooms", rooms);
        
        // Tính toán phần trăm thiết bị online
        if (stats.getTotalThietBi() > 0) {
            int onlinePercent = (int) ((stats.getDevicesOnline() * 100.0) / stats.getTotalThietBi());
            int offlinePercent = 100 - onlinePercent;
            model.addAttribute("onlinePercent", onlinePercent);
            model.addAttribute("offlinePercent", offlinePercent);
        } else {
            model.addAttribute("onlinePercent", 0);
            model.addAttribute("offlinePercent", 0);
        }
        
        return "dashboard";
    }
    
    @GetMapping("/dashboard/logout")
    public String logout() {
        // Clear localStorage và redirect về login
        return "redirect:/auth/login";
    }
    
    @GetMapping("/dashboard/room/{roomId}")
    public String roomDetail(@PathVariable Long roomId, Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        
        // Nếu chưa login thì chuyển đến trang login
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        // Set title và user info
        model.addAttribute("title", "Chi tiết khu vực - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        
        // Lấy thông tin chi tiết khu vực
        RoomDTO room = dashboardService.getRoomDetail(roomId);
        model.addAttribute("room", room);
        
        return "room-detail";
    }
}
