package com.iot.management.controller.ui;

import com.iot.management.model.entity.CauHinhHeThong;
import com.iot.management.service.CauHinhHeThongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @Autowired
    private CauHinhHeThongService cauHinhService;

    @GetMapping
    public String getSettings(Model model) {
        // Khởi tạo cấu hình mặc định nếu chưa có
        if (cauHinhService.getAllCauHinh().isEmpty()) {
            cauHinhService.initDefaultConfig();
        }
        
        // Lấy tất cả cấu hình hiện tại
        List<CauHinhHeThong> allConfigs = cauHinhService.getAllCauHinh();
        model.addAttribute("configs", allConfigs);
        
        // Lấy từng cấu hình cụ thể để hiển thị
        model.addAttribute("appName", cauHinhService.getGiaTri("app.name", "IoT Device Manager"));
        model.addAttribute("appVersion", cauHinhService.getGiaTri("app.version", "1.0.0"));
        model.addAttribute("appDescription", cauHinhService.getGiaTri("app.description", "Hệ thống quản lý thiết bị IoT"));
        
        // Thông báo
        model.addAttribute("notifEmailEnabled", Boolean.parseBoolean(cauHinhService.getGiaTri("notification.email.enabled", "false")));
        model.addAttribute("notifSoundEnabled", Boolean.parseBoolean(cauHinhService.getGiaTri("notification.sound.enabled", "false")));
        model.addAttribute("notifDeviceEnabled", Boolean.parseBoolean(cauHinhService.getGiaTri("notification.device.enabled", "true")));
        
        // Giao diện
        model.addAttribute("uiTheme", cauHinhService.getGiaTri("ui.theme", "light"));
        model.addAttribute("uiLanguage", cauHinhService.getGiaTri("ui.language", "vi"));
        
        // Bảo mật
        model.addAttribute("security2FAEnabled", Boolean.parseBoolean(cauHinhService.getGiaTri("security.2fa.enabled", "false")));
        model.addAttribute("securitySessionTimeout", cauHinhService.getGiaTri("security.session.timeout", "30"));
        
        // Dữ liệu
        model.addAttribute("dataBackupEnabled", Boolean.parseBoolean(cauHinhService.getGiaTri("data.backup.enabled", "false")));
        model.addAttribute("dataRetentionDays", cauHinhService.getGiaTri("data.retention.days", "30"));
        
        // Hỗ trợ
        model.addAttribute("supportEmail", cauHinhService.getGiaTri("support.email", "support@iotmanager.com"));
        
        return "settings";
    }

    @PostMapping("/update")
    public String updateSettings(
            @RequestParam(required = false) String notifEmailEnabled,
            @RequestParam(required = false) String notifSoundEnabled,
            @RequestParam(required = false) String notifDeviceEnabled,
            @RequestParam(required = false) String uiTheme,
            @RequestParam(required = false) String uiLanguage,
            @RequestParam(required = false) String security2FAEnabled,
            @RequestParam(required = false) String securitySessionTimeout,
            @RequestParam(required = false) String dataBackupEnabled,
            @RequestParam(required = false) String dataRetentionDays,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Cập nhật thông báo
            if (notifEmailEnabled != null) {
                cauHinhService.saveCauHinh("notification.email.enabled", notifEmailEnabled);
            }
            if (notifSoundEnabled != null) {
                cauHinhService.saveCauHinh("notification.sound.enabled", notifSoundEnabled);
            }
            if (notifDeviceEnabled != null) {
                cauHinhService.saveCauHinh("notification.device.enabled", notifDeviceEnabled);
            }
            
            // Cập nhật giao diện
            if (uiTheme != null) {
                cauHinhService.saveCauHinh("ui.theme", uiTheme);
            }
            if (uiLanguage != null) {
                cauHinhService.saveCauHinh("ui.language", uiLanguage);
            }
            
            // Cập nhật bảo mật
            if (security2FAEnabled != null) {
                cauHinhService.saveCauHinh("security.2fa.enabled", security2FAEnabled);
            }
            if (securitySessionTimeout != null) {
                cauHinhService.saveCauHinh("security.session.timeout", securitySessionTimeout);
            }
            
            // Cập nhật dữ liệu
            if (dataBackupEnabled != null) {
                cauHinhService.saveCauHinh("data.backup.enabled", dataBackupEnabled);
            }
            if (dataRetentionDays != null) {
                cauHinhService.saveCauHinh("data.retention.days", dataRetentionDays);
            }
            
            redirectAttributes.addFlashAttribute("success", "Đã lưu cấu hình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        
        return "redirect:/settings";
    }
}

