package com.iot.management.controller.api.device;

import com.iot.management.exception.ErrorResponse;
import com.iot.management.exception.PackageLimitExceededException;
import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.DuAn;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.model.entity.LoaiThietBi;
import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.service.CauHinhTruongDuLieuService;
import com.iot.management.service.KhuVucService;
import com.iot.management.service.LoaiThietBiService;
import com.iot.management.service.PackageLimitService;
import com.iot.management.service.ThietBiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Arrays;

@Controller
@RequestMapping("/thiet-bi")
public class ThietBiController {

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

    private final ThietBiRepository thietBiRepository;
    private final KhuVucRepository khuVucRepository;
    public ThietBiController(ThietBiRepository thietBiRepository, KhuVucRepository khuVucRepository) {
        this.thietBiRepository = thietBiRepository;
        this.khuVucRepository = khuVucRepository;
    }

    @GetMapping("/khu-vuc/{maKhuVuc}")
    public String getDevicesInArea(@PathVariable Long maKhuVuc, Model model) {
        KhuVuc khuVuc = khuVucService.getKhuVucById(maKhuVuc);
        List<LoaiThietBi> loaiThietBis = loaiThietBiService.findAllDeviceTypes();
        
        model.addAttribute("khuVuc", khuVuc);
        model.addAttribute("loaiThietBis", loaiThietBis);
        
        return "thiet-bi/khu-vuc-detail";
    }

    // API endpoints
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createDevice(@RequestBody ThietBi thietBi) {
       try {
        // Lấy khu vực
        if (thietBi.getKhuVuc() == null || thietBi.getKhuVuc().getMaKhuVuc() == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "INVALID_REQUEST", "Khu vực không được để trống"));
        }

        KhuVuc khuVuc = khuVucRepository.findById(thietBi.getKhuVuc().getMaKhuVuc())
                .orElseThrow(() -> new RuntimeException("Khu vực không tồn tại"));

        // Lấy dự án từ khu vực
        DuAn duAn = khuVuc.getDuAn();
        if (duAn == null) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(400, "INVALID_REQUEST", "Khu vực chưa được gán vào dự án nào"));
        }

        // Kiểm tra giới hạn thiết bị theo gói cước
        packageLimitService.validateDeviceLimit(duAn);

        // Set lại khu vực và trạng thái mặc định
        thietBi.setKhuVuc(khuVuc);
        if (thietBi.getTrangThai() == null) thietBi.setTrangThai("HOAT_DONG");

        // Lưu thiết bị
        ThietBi saved = thietBiRepository.save(thietBi);

        return ResponseEntity.ok(saved);
    } catch (PackageLimitExceededException ex) {
        return ResponseEntity.status(400)
                .body(new ErrorResponse(400, "LIMIT_EXCEEDED", ex.getMessage()));
    } catch (Exception ex) {
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
        @RequestBody Map<String, String> payload) {
    try {
        String trangThai = payload.get("trangThai"); // "hoat_dong" hoặc "tat"
        thietBiService.capNhatTrangThaiThietBi(id, trangThai);

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
