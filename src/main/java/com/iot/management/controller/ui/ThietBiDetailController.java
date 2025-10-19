package com.iot.management.controller.ui;

import com.iot.management.model.entity.ThietBi;
import com.iot.management.service.ThietBiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller("thietBiDetailUiController")
@RequestMapping("/thiet-bi")
public class ThietBiDetailController {

    private final ThietBiService thietBiService;

    public ThietBiDetailController(ThietBiService thietBiService) {
        this.thietBiService = thietBiService;
    }

    @GetMapping("/{id}")
    public String getDeviceDetail(@PathVariable Long id, Model model) {
        Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
        
        if (deviceOpt.isEmpty()) {
            return "redirect:/thiet-bi?error=Device+not+found";
        }
        
        ThietBi device = deviceOpt.get();
        
        // Add device info to model
        model.addAttribute("device", device);
        model.addAttribute("deviceId", id);
        
        // Add device type group for conditional rendering of controls
        String nhomThietBi = device.getLoaiThietBi() != null && device.getLoaiThietBi().getNhomThietBi() != null 
            ? device.getLoaiThietBi().getNhomThietBi().name() 
            : "SENSOR";
        model.addAttribute("nhomThietBi", nhomThietBi);
        
        return "device-detail";
    }
}
