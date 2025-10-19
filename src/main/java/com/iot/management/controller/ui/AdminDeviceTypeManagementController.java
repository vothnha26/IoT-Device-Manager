package com.iot.management.controller.ui;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/device-types")
@PreAuthorize("hasRole('MANAGER')")
public class AdminDeviceTypeManagementController {

    @GetMapping
    public String showDeviceTypeManagement() {
        return "admin/device-type-management";
    }
}
