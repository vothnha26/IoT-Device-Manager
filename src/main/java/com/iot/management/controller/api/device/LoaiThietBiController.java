package com.iot.management.controller.api.device;

import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.service.LoaiThietBiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-types")
public class LoaiThietBiController {

    private final LoaiThietBiService loaiThietBiService;

    public LoaiThietBiController(LoaiThietBiService loaiThietBiService) {
        this.loaiThietBiService = loaiThietBiService;
    }

    // Endpoint công khai cho user xem, nhưng chỉ Manager mới được tạo
    @GetMapping
    public ResponseEntity<List<LoaiThietBi>> getAllDeviceTypes() {
        List<LoaiThietBi> deviceTypes = loaiThietBiService.findAllDeviceTypes();
        return ResponseEntity.ok(deviceTypes);
    }

    @PostMapping
    // @PreAuthorize("hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
    public ResponseEntity<LoaiThietBi> createDeviceType(@RequestBody LoaiThietBi loaiThietBi) {
        LoaiThietBi createdType = loaiThietBiService.createDeviceType(loaiThietBi);
        return new ResponseEntity<>(createdType, HttpStatus.CREATED);
    }
}