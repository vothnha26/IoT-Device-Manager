package com.iot.management.controller.ui;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.service.DevicePermissionService;
import com.iot.management.service.NguoiDungService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/device-permissions")
@PreAuthorize("hasRole('ADMIN')")
public class DevicePermissionController {

    private final DevicePermissionService devicePermissionService;
    private final NguoiDungService nguoiDungService;

    public DevicePermissionController(DevicePermissionService devicePermissionService,
                                      NguoiDungService nguoiDungService) {
        this.devicePermissionService = devicePermissionService;
        this.nguoiDungService = nguoiDungService;
    }
    @GetMapping
    public String showUserList(Model model) {
        List<NguoiDung> users = nguoiDungService.findAllUsers()
                .stream()
                .filter(u -> u.getVaiTro() == null || u.getVaiTro().stream()
                        .noneMatch(v -> "ROLE_ADMIN".equals(v.getTenVaiTro())))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        return "admin/device-permissions/user-list";
    }

    @GetMapping("/{userId}")
    public String manageUserDevicePermissions(@PathVariable Long userId, Model model) {
        NguoiDung user = nguoiDungService.getById(userId);
        List<ThietBi> userDevices = devicePermissionService.getNguoiDungAccessibleDevices(userId);
        
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("userDevices", userDevices);
        return "admin/device-permissions/manage";
    }

    @PostMapping("/grant")
    @ResponseBody
    public String grantAccess(@RequestParam Long userId, @RequestParam Long deviceId) {
        devicePermissionService.grantDeviceAccess(userId, deviceId);
        return "success";
    }

    @PostMapping("/revoke")
    @ResponseBody
    public String revokeAccess(@RequestParam Long userId, @RequestParam Long deviceId) {
        devicePermissionService.revokeDeviceAccess(userId, deviceId);
        return "success";
    }

    @GetMapping("/api/devices/available")
    @ResponseBody
    public List<ThietBi> getAvailableDevices() {
        return devicePermissionService.getAllDevices();
    }
}