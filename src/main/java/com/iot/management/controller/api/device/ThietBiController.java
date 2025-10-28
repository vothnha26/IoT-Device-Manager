package com.iot.management.controller.api.device;

import com.iot.management.exception.ErrorResponse;
import com.iot.management.exception.PackageLimitExceededException;
import com.iot.management.model.entity.*;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.service.CauHinhTruongDuLieuService;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.LoaiThietBiService;
import com.iot.management.service.NhatKyDuLieuService;
import com.iot.management.service.PackageLimitService;
import com.iot.management.service.ThietBiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Arrays;

import com.iot.management.websocket.DeviceMessagingService;
import com.iot.management.model.repository.LenhDieuKhienRepository;
import com.iot.management.model.entity.LenhDieuKhien;

@Controller
@RequestMapping("/thiet-bi")
public class ThietBiController {
    private static final Logger logger = LoggerFactory.getLogger(ThietBiController.class);

    @Autowired
    private ThietBiService thietBiService;

    @Autowired
    private KhuVucService khuVucService;

    @Autowired
    private LoaiThietBiService loaiThietBiService;

    @Autowired
    private CauHinhTruongDuLieuService cauHinhTruongDuLieuService;

    @Autowired
    private PackageLimitService packageLimitService;

    @Autowired(required = false)
    private DeviceMessagingService deviceMessagingService;
    
    @Autowired
    private NhatKyDuLieuService nhatKyDuLieuService;

    @Autowired
    private com.iot.management.service.TuDongHoaService tuDongHoaService;
    
    @Autowired
    private com.iot.management.service.PhanQuyenService phanQuyenService;
    
    @Autowired
    private com.iot.management.model.repository.ThietBiRepository thietBiRepository;

    private final KhuVucRepository khuVucRepository;
    private final NguoiDungRepository nguoiDungRepository;
    @Autowired
    private LenhDieuKhienRepository lenhDieuKhienRepository;
    
    public ThietBiController(KhuVucRepository khuVucRepository,
                            NguoiDungRepository nguoiDungRepository) {
        this.khuVucRepository = khuVucRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @GetMapping("/khu-vuc/{maKhuVuc}")
    public String getDevicesInArea(@PathVariable Long maKhuVuc, Model model, Authentication authentication) {
        // Get authenticated user
        if (authentication == null) {
            return "redirect:/auth/login";
        }
        
        String email = authentication.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get area and device types
        KhuVuc khuVuc = khuVucService.getKhuVucById(maKhuVuc);
        List<LoaiThietBi> loaiThietBis = loaiThietBiService.findAllDeviceTypes();
        
        // Get user's active package
        DuAn duAn = khuVuc.getDuAn();
        if (duAn != null) {
            try {
                DangKyGoi dangKyGoi = packageLimitService.getCurrentPackage(duAn);
                if (dangKyGoi != null && dangKyGoi.getGoiCuoc() != null) {
                    // Add user with package to model
                    model.addAttribute("userPackage", dangKyGoi.getGoiCuoc());
                }
            } catch (Exception e) {
                System.out.println("⚠️ Could not get package for user: " + e.getMessage());
            }
        }
        
        model.addAttribute("user", user);
        model.addAttribute("khuVuc", khuVuc);
        model.addAttribute("loaiThietBis", loaiThietBis);
        
        // Kiểm tra vai trò của user trong dự án
        boolean laChuSoHuu = false;
        if (duAn != null) {
            com.iot.management.model.enums.DuAnRole vaiTroDuAn = phanQuyenService.layVaiTroDuAn(duAn.getMaDuAn(), user.getMaNguoiDung());
            laChuSoHuu = (vaiTroDuAn == com.iot.management.model.enums.DuAnRole.CHU_SO_HUU);
        }
        
        // Kiểm tra quyền của user với từng thiết bị trong khu vực
        java.util.Map<Long, Boolean> quyenDieuKhienThietBi = new java.util.HashMap<>();
        java.util.Map<Long, Boolean> quyenXemDuLieuThietBi = new java.util.HashMap<>();
        java.util.Set<ThietBi> thietBiCoQuyenXem = new java.util.HashSet<>();
        
        // Lấy tất cả thiết bị trong khu vực
        List<ThietBi> thietBis = thietBiRepository.findByKhuVuc_MaKhuVuc(maKhuVuc);
        for (ThietBi tb : thietBis) {
            // CHU_SO_HUU thấy tất cả thiết bị, không cần check quyền
            if (laChuSoHuu) {
                thietBiCoQuyenXem.add(tb);
                quyenDieuKhienThietBi.put(tb.getMaThietBi(), true);
                quyenXemDuLieuThietBi.put(tb.getMaThietBi(), true);
            } else {
                // Người dùng khác phải check quyền
                boolean coQuyenXem = phanQuyenService.kiemTraQuyenXemDuLieuThietBi(tb.getMaThietBi(), user.getMaNguoiDung());
                
                // Chỉ thêm thiết bị vào danh sách nếu có quyền xem
                if (coQuyenXem) {
                    boolean coQuyenDieuKhien = phanQuyenService.kiemTraQuyenDieuKhienThietBi(tb.getMaThietBi(), user.getMaNguoiDung());
                    
                    thietBiCoQuyenXem.add(tb);
                    quyenDieuKhienThietBi.put(tb.getMaThietBi(), coQuyenDieuKhien);
                    quyenXemDuLieuThietBi.put(tb.getMaThietBi(), coQuyenXem);
                }
            }
        }
        
        // Cập nhật danh sách thiết bị trong khu vực (chỉ thiết bị có quyền xem)
        khuVuc.setThietBis(thietBiCoQuyenXem);
        
        model.addAttribute("quyenDieuKhienThietBi", quyenDieuKhienThietBi);
        model.addAttribute("quyenXemDuLieuThietBi", quyenXemDuLieuThietBi);
        
        return "thiet-bi/khu-vuc-detail";
    }

    @GetMapping("/khu-vuc/{maKhuVuc}/thong-ke")
    public String getDevicesStatsInArea(@PathVariable Long maKhuVuc, Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        KhuVuc khuVuc = khuVucService.getKhuVucById(maKhuVuc);
        model.addAttribute("khuVuc", khuVuc);

        // Optional: pass known device IDs for LEDs and sensor if you want dynamic mapping later
        // For now, keep the simple mapping used elsewhere
        model.addAttribute("sensorDeviceId", 4L);
        model.addAttribute("switchDeviceIds", java.util.List.of(2L, 3L));

        return "thiet-bi/khu-vuc-stats";
    }

    @GetMapping("/{maThietBi}/rules")
    public String getDeviceRules(@PathVariable Long maThietBi, Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        String email = authentication.getName();
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get device
        ThietBi thietBi = thietBiService.findDeviceById(maThietBi)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại"));

        // Verify ownership
        if (thietBi.getChuSoHuu() != null && 
            !thietBi.getChuSoHuu().getMaNguoiDung().equals(user.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền truy cập thiết bị này");
        }

        // Get rules for this device
        List<LuatNguong> rules = tuDongHoaService.findRulesByDevice(maThietBi);

        model.addAttribute("user", user);
        model.addAttribute("thietBi", thietBi);
        model.addAttribute("rules", rules);

        return "thiet-bi/rules";
    }

    // API endpoints
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createDevice(@RequestBody ThietBi thietBi) {
       try {
        System.out.println("📥 Creating new device: " + thietBi.getTenThietBi());
        
        // Validate khu vực
        if (thietBi.getKhuVuc() == null || thietBi.getKhuVuc().getMaKhuVuc() == null) {
            System.out.println("❌ Missing area ID");
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "INVALID_REQUEST", "Khu vực không được để trống"));
        }

        // Lấy khu vực
        KhuVuc khuVuc = khuVucRepository.findById(thietBi.getKhuVuc().getMaKhuVuc())
                .orElseThrow(() -> {
                    System.out.println("❌ Area not found: " + thietBi.getKhuVuc().getMaKhuVuc());
                    return new RuntimeException("Khu vực không tồn tại");
                });
        
        System.out.println("✅ Found area: " + khuVuc.getTenKhuVuc());

        // Lấy dự án từ khu vực
        DuAn duAn = khuVuc.getDuAn();
        if (duAn == null) {
            System.out.println("❌ Area not assigned to any project");
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "INVALID_REQUEST", "Khu vực chưa được gán vào dự án nào"));
        }
        
        System.out.println("✅ Project: " + duAn.getTenDuAn());

        // Kiểm tra giới hạn thiết bị theo gói cước
        packageLimitService.validateDeviceLimit(duAn);
        System.out.println("✅ Device limit check passed");

        // Validate loại thiết bị (nếu có)
        if (thietBi.getLoaiThietBi() != null && thietBi.getLoaiThietBi().getMaLoaiThietBi() != null) {
            // Keep the reference to device type
            System.out.println("✅ Device type: " + thietBi.getLoaiThietBi().getMaLoaiThietBi());
        }

        // Gán khu vực cho thiết bị
        thietBi.setKhuVuc(khuVuc);

        // Lấy chủ sở hữu từ khu vực (bắt buộc với cột ma_chu_so_huu)
        if (khuVuc.getChuSoHuu() == null || khuVuc.getChuSoHuu().getMaNguoiDung() == null) {
            System.out.println("❌ Area has no owner assigned, cannot create device");
            return ResponseEntity.status(400)
                    .body(new ErrorResponse(400, "INVALID_AREA_OWNER", "Khu vực chưa có chủ sở hữu"));
        }

        Long ownerId = khuVuc.getChuSoHuu().getMaNguoiDung();
        System.out.println("✅ Owner ID: " + ownerId);

        // Tạo thiết bị qua service để auto sinh token, mặc định trạng thái, ngày lắp đặt...
        ThietBi saved = thietBiService.createDevice(ownerId, thietBi);
        System.out.println("✅ Device saved successfully with ID: " + saved.getMaThietBi());

        return ResponseEntity.ok(saved);
        
    } catch (PackageLimitExceededException ex) {
        System.out.println("⚠️ Package limit exceeded: " + ex.getMessage());
        return ResponseEntity.status(400)
                .body(new ErrorResponse(400, "LIMIT_EXCEEDED", ex.getMessage()));
                
    } catch (Exception ex) {
        System.err.println("❌ Error creating device: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.status(500)
                .body(new ErrorResponse(500, "SERVER_ERROR", ex.getMessage()));
    }
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody ThietBi thietBi) {
        try {
            thietBi.setMaThietBi(id);
            ThietBi updatedDevice = thietBiService.updateDevice(id,thietBi);
            return ResponseEntity.ok(updatedDevice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteDevice(@PathVariable Long id) {
        try {
            thietBiService.deleteDevice(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/state")
    @ResponseBody
    public ResponseEntity<?> toggleDeviceState(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload,
        Authentication authentication) {
        try {
            String trangThai = payload.get("trangThai"); // "hoat_dong" hoặc "tat"
            logger.info("🎯 [API] PUT /thiet-bi/{}/state - Request: {}", id, trangThai);
            
            // capNhatTrangThaiThietBi đã tự động gửi lệnh xuống ESP32 qua WebSocket
            thietBiService.capNhatTrangThaiThietBi(id, trangThai);

            // Xác định trạng thái boolean để lưu log
            boolean isOn = false;
            String fieldName = "trang_thai";
            
            if (trangThai != null) {
                String normalized = trangThai.trim().toLowerCase();
                isOn = normalized.equals("hoat_dong") || normalized.equals("on") || normalized.equals("bat");
                
                // Thiết lập field name cho LED1 và LED2
                if (id == 1) {
                    fieldName = "led1";
                } else if (id == 2) {
                    fieldName = "led2";
                } else if (id == 3) {
                    fieldName = "led_device_3";
                }
                
                // Lưu nhật ký điều khiển thủ công vào bảng NhatKyDuLieu
                try {
                    nhatKyDuLieuService.saveManualControlLog(id, fieldName, isOn);
                    System.out.println("📝 Saved manual control log for device " + id + " (field: " + fieldName + "): " + trangThai);
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to save control log: " + e.getMessage());
                    // Không throw exception để không ảnh hưởng đến điều khiển thiết bị
                }

                // Lưu lịch sử lệnh điều khiển kèm người dùng (LenhDieuKhien)
                try {
                    Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
                    if (deviceOpt.isPresent()) {
                        LenhDieuKhien lenh = new LenhDieuKhien();
                        lenh.setThietBi(deviceOpt.get());
                        // Tên lệnh theo trường điều khiển để dễ tra cứu
                        lenh.setTenLenh("toggle_" + fieldName);
                        // Giá trị lệnh: giữ nguyên chuỗi trạng thái gửi xuống
                        lenh.setGiaTriLenh(normalized);
                        // Trạng thái thực thi của lệnh
                        lenh.setTrangThai("executed");

                        // Gán người dùng nếu có xác thực
                        if (authentication != null) {
                            String email = authentication.getName();
                            nguoiDungRepository.findByEmail(email).ifPresent(lenh::setNguoiGui);
                        }

                        lenhDieuKhienRepository.save(lenh);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to save command history (LenhDieuKhien): " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                    "maThietBi", id,
                    "trangThai", trangThai
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Điều khiển LED1 (GPIO2) - legacy endpoint
    @PutMapping("/{id}/led1")
    @ResponseBody
    public ResponseEntity<?> toggleLED1(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Authentication authentication) {
        try {
            // Chấp nhận cả boolean và string
            Object stateObj = payload.get("state");
            Object trangThaiObj = payload.get("trangThai");
            
            boolean isOn = false;
            String trangThai = "tat";
            
            if (trangThaiObj != null) {
                trangThai = trangThaiObj.toString().trim().toLowerCase();
                isOn = trangThai.equals("hoat_dong");
            } else if (stateObj != null) {
                isOn = Boolean.parseBoolean(stateObj.toString());
                trangThai = isOn ? "hoat_dong" : "tat";
            }
            
            String command = trangThai;
            
            // Cập nhật database
            thietBiService.capNhatTrangThaiThietBi(id, trangThai);
            
            // Gửi lệnh qua WebSocket
            if (deviceMessagingService != null) {
                boolean sent = deviceMessagingService.sendCommandToDevice(id, command);
                System.out.println("📡 Sent '" + command + "' to device " + id + ": " + (sent ? "✅ Success" : "❌ Failed"));
            }
            
            // Lưu nhật ký
            try {
                nhatKyDuLieuService.saveManualControlLog(id, "led1", isOn);
                System.out.println("📝 Saved LED1 control log for device " + id + ": " + trangThai);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save LED1 control log: " + e.getMessage());
            }

            // Lưu lịch sử lệnh điều khiển kèm người dùng (LenhDieuKhien)
            try {
                Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
                if (deviceOpt.isPresent()) {
                    LenhDieuKhien lenh = new LenhDieuKhien();
                    lenh.setThietBi(deviceOpt.get());
                    lenh.setTenLenh("toggle_led1");
                    lenh.setGiaTriLenh(trangThai);
                    lenh.setTrangThai("executed");
                    if (authentication != null) {
                        String email = authentication.getName();
                        nguoiDungRepository.findByEmail(email).ifPresent(lenh::setNguoiGui);
                    }
                    lenhDieuKhienRepository.save(lenh);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save command history (LenhDieuKhien - led1): " + e.getMessage());
            }
            
            return ResponseEntity.ok(Map.of(
                    "maThietBi", id,
                    "led", "led1",
                    "trangThai", trangThai,
                    "state", isOn
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // Điều khiển LED2 (GPIO4) - legacy endpoint
    @PutMapping("/{id}/led2")
    @ResponseBody
    public ResponseEntity<?> toggleLED2(
        @PathVariable Long id,
        @RequestBody Map<String, Object> payload,
        Authentication authentication) {
        try {
            // Chấp nhận cả boolean và string
            Object stateObj = payload.get("state");
            Object trangThaiObj = payload.get("trangThai");
            
            boolean isOn = false;
            String trangThai = "tat";
            
            if (trangThaiObj != null) {
                trangThai = trangThaiObj.toString().trim().toLowerCase();
                isOn = trangThai.equals("hoat_dong");
            } else if (stateObj != null) {
                isOn = Boolean.parseBoolean(stateObj.toString());
                trangThai = isOn ? "hoat_dong" : "tat";
            }
            
            String command = trangThai;
            
            // Cập nhật database
            thietBiService.capNhatTrangThaiThietBi(id, trangThai);
            
            // Gửi lệnh qua WebSocket
            if (deviceMessagingService != null) {
                boolean sent = deviceMessagingService.sendCommandToDevice(id, command);
                System.out.println("📡 Sent '" + command + "' to device " + id + ": " + (sent ? "✅ Success" : "❌ Failed"));
            }
            
            // Lưu nhật ký
            try {
                nhatKyDuLieuService.saveManualControlLog(id, "led2", isOn);
                System.out.println("📝 Saved LED2 control log for device " + id + ": " + trangThai);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save LED2 control log: " + e.getMessage());
            }

            // Lưu lịch sử lệnh điều khiển kèm người dùng (LenhDieuKhien)
            try {
                Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
                if (deviceOpt.isPresent()) {
                    LenhDieuKhien lenh = new LenhDieuKhien();
                    lenh.setThietBi(deviceOpt.get());
                    lenh.setTenLenh("toggle_led2");
                    lenh.setGiaTriLenh(trangThai);
                    lenh.setTrangThai("executed");
                    if (authentication != null) {
                        String email = authentication.getName();
                        nguoiDungRepository.findByEmail(email).ifPresent(lenh::setNguoiGui);
                    }
                    lenhDieuKhienRepository.save(lenh);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Failed to save command history (LenhDieuKhien - led2): " + e.getMessage());
            }
            
            return ResponseEntity.ok(Map.of(
                    "maThietBi", id,
                    "led", "led2",
                    "trangThai", trangThai,
                    "state", isOn
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> layThietBiTheoId(@PathVariable Long id) {
    Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
    if (deviceOpt.isPresent()) {
        return ResponseEntity.ok(deviceOpt.get());
    } else {
        return ResponseEntity.notFound().build();
    }
}

    @GetMapping("/{id}/config")
    @ResponseBody
    public ResponseEntity<?> layCauHinhThietBi(@PathVariable Long id) {
    Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
    if (deviceOpt.isPresent()) {
        ThietBi device = deviceOpt.get();
        List<CauHinhTruongDuLieu> configs = cauHinhTruongDuLieuService
            .layCauHinhHienThiTheoLoaiThietBi(device.getLoaiThietBi().getMaLoaiThietBi());

        // Map sang JSON theo JS mong muốn
        List<Map<String, Object>> fields = configs.stream().map(c -> Map.of(
            "key", c.getTenTruong(),
            "label", c.getTenHienThi(),
            "type", mapKieuDuLieu(c.getKieuDuLieu()), // ví dụ 'range', 'select', ...
            "value", c.getGiaTriMin(), // default hoặc giá trị hiện tại
            "unit", c.getDonVi(),
            "options", parseOptions(c.getGhiChu())
        )).toList();

        return ResponseEntity.ok(Map.of("fields", fields));
    } else {
        return ResponseEntity.notFound().build();
    }
}

    private String mapKieuDuLieu(String kieu) {
        switch(kieu.toLowerCase()) {
            case "integer": case "float": case "double": return "range";
            case "boolean": return "toggle";
            case "enum": return "select";
            default: return "text";
        }
    }

    private List<Map<String,String>> parseOptions(String ghiChu) {
        if (ghiChu == null || ghiChu.isEmpty()) return List.of();
        return Arrays.stream(ghiChu.split(","))
                    .map(v -> Map.of("value", v.trim(), "label", v.trim()))
                    .toList();
    }

    /**
     * Kiểm tra trạng thái kết nối WebSocket của thiết bị
     */
    @GetMapping("/{id}/connection-status")
    @ResponseBody
    public ResponseEntity<?> checkConnectionStatus(@PathVariable Long id) {
        try {
            // Kiểm tra thiết bị có tồn tại không
            Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Kiểm tra kết nối WebSocket
            boolean isConnected = false;
            String message = "Device is offline";
            
            if (deviceMessagingService != null) {
                var registry = deviceMessagingService.getRegistry();
                if (registry != null) {
                    isConnected = registry.isOnline(id);
                    message = isConnected ? "Device is online and ready" : "Device is not connected to WebSocket";
                }
            }
            
            logger.info("🔍 Connection status for device {}: {}", id, isConnected ? "ONLINE" : "OFFLINE");
            
            return ResponseEntity.ok(Map.of(
                "deviceId", id,
                "connected", isConnected,
                "status", isConnected ? "online" : "offline",
                "message", message
            ));
        } catch (Exception e) {
            logger.error("❌ Error checking connection status for device {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }


    @PutMapping("/{id}/config")
    @ResponseBody
    public ResponseEntity<?> saveDeviceConfig(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            Optional<ThietBi> deviceOpt = thietBiService.findDeviceById(id);
            if (deviceOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ThietBi device = deviceOpt.get();

            // payload: { "fieldKey1": value1, "fieldKey2": value2, ... }
            // Gọi service để lưu
            cauHinhTruongDuLieuService.luuCauHinhThietBi(device, payload);

            return ResponseEntity.ok(Map.of(
                    "maThietBi", id,
                    "status", "success"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}
