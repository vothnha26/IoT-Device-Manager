package com.iot.management.controller.api.device;

import com.iot.management.controller.ControllerHelper;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.service.ThietBiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
@PreAuthorize("hasRole('USER') or hasRole('MANAGER')")
public class ThietBiController {

    private final ThietBiService thietBiService;
    private final ControllerHelper controllerHelper;

    public ThietBiController(ThietBiService thietBiService, ControllerHelper controllerHelper) {
        this.thietBiService = thietBiService;
        this.controllerHelper = controllerHelper;
    }

    @GetMapping
    public ResponseEntity<?> getMyDevices(Principal principal) {
        try {
            Long userId = controllerHelper.getUserIdFromPrincipal(principal);
            List<ThietBi> devices = thietBiService.findDevicesByOwner(userId);
            return ResponseEntity.ok(devices);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Lỗi khi lấy danh sách thiết bị: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống khi lấy danh sách thiết bị");
        }
    }

    @PostMapping
    public ResponseEntity<?> createDevice(@RequestBody ThietBi thietBi, Principal principal) {
        try {
            if (thietBi == null) {
                return ResponseEntity.badRequest().body("Dữ liệu thiết bị không được để trống");
            }

            Long userId = controllerHelper.getUserIdFromPrincipal(principal);
            ThietBi createdDevice = thietBiService.createDevice(userId, thietBi);
            return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Lỗi khi tạo thiết bị: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống khi tạo thiết bị");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable("id") Long id, Principal principal) {
        try {
            Long userId = controllerHelper.getUserIdFromPrincipal(principal);
            ThietBi device = thietBiService.findDeviceById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

            if (device.getChuSoHuu() == null || !device.getChuSoHuu().getMaNguoiDung().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không có quyền truy cập thiết bị này");
            }
            
            return ResponseEntity.ok(device);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Không tìm thấy thiết bị: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi truy xuất thiết bị");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable("id") Long id, Principal principal) {
        try {
            Long userId = controllerHelper.getUserIdFromPrincipal(principal);
            ThietBi device = thietBiService.findDeviceById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

            if (device.getChuSoHuu() == null || !device.getChuSoHuu().getMaNguoiDung().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không có quyền xóa thiết bị này");
            }

            thietBiService.deleteDevice(id);
            return ResponseEntity.ok("Xóa thiết bị thành công!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Không tìm thấy thiết bị: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi xóa thiết bị");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDevice(
            @PathVariable("id") Long id,
            @RequestBody ThietBi thietBi,
            Principal principal) {
        try {
            Long userId = controllerHelper.getUserIdFromPrincipal(principal);
            ThietBi existingDevice = thietBiService.findDeviceById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

            // Kiểm tra quyền
            if (existingDevice.getChuSoHuu() == null || !existingDevice.getChuSoHuu().getMaNguoiDung().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không có quyền cập nhật thiết bị này");
            }

            ThietBi updatedDevice = thietBiService.updateDevice(id, thietBi);
            return ResponseEntity.ok(updatedDevice);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Lỗi khi cập nhật thiết bị: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống khi cập nhật thiết bị");
        }
    }
}