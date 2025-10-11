package com.iot.management.controller.api.admin;

import com.iot.management.model.entity.PhienBanFirmware;
import com.iot.management.service.PhienBanFirmwareService; // Giả sử đã có service này
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/firmware")
// @PreAuthorize("hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
public class PhienBanFirmwareController {

    private final PhienBanFirmwareService phienBanFirmwareService;

    public PhienBanFirmwareController(PhienBanFirmwareService phienBanFirmwareService) {
        this.phienBanFirmwareService = phienBanFirmwareService;
    }

    @PostMapping
    public ResponseEntity<PhienBanFirmware> uploadFirmware(@RequestBody PhienBanFirmware phienBanFirmware) {
        // Logic thực tế có thể sẽ nhận một MultipartFile để tải file .bin lên server
        // Ở đây, chúng ta chỉ lưu metadata vào CSDL
        PhienBanFirmware savedFirmware = phienBanFirmwareService.save(phienBanFirmware);
        return new ResponseEntity<>(savedFirmware, HttpStatus.CREATED);
    }
}