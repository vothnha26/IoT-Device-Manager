package com.iot.management.controller.api.user;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.ThongBao;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.service.ThongBaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NguoiDungRepository nguoiDungRepository;

    public ThongBaoController(ThongBaoService thongBaoService, 
                             NguoiDungRepository nguoiDungRepository) {
        this.thongBaoService = thongBaoService;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    // Lấy tất cả thông báo
    @GetMapping
    public ResponseEntity<List<ThongBao>> getAllNotifications(Principal principal) {
        NguoiDung user = getUserFromPrincipal(principal);
        List<ThongBao> notifications = thongBaoService.getAllByUser(user);
        return ResponseEntity.ok(notifications);
    }

    // Lấy thông báo chưa đọc
    @GetMapping("/unread")
    public ResponseEntity<List<ThongBao>> getUnreadNotifications(Principal principal) {
        NguoiDung user = getUserFromPrincipal(principal);
        List<ThongBao> notifications = thongBaoService.getUnreadByUser(user);
        return ResponseEntity.ok(notifications);
    }

    // Đếm thông báo chưa đọc
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(Principal principal) {
        NguoiDung user = getUserFromPrincipal(principal);
        Long count = thongBaoService.countUnread(user);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // Lấy N thông báo mới nhất
    @GetMapping("/latest")
    public ResponseEntity<List<ThongBao>> getLatestNotifications(
            Principal principal,
            @RequestParam(defaultValue = "10") int limit) {
        NguoiDung user = getUserFromPrincipal(principal);
        List<ThongBao> notifications = thongBaoService.getLatest(user, limit);
        return ResponseEntity.ok(notifications);
    }

    // Đánh dấu đã đọc
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        thongBaoService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    // Đánh dấu tất cả đã đọc
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Principal principal) {
        NguoiDung user = getUserFromPrincipal(principal);
        thongBaoService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }

    // Xóa thông báo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        thongBaoService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    // Tạo thông báo test (chỉ cho dev)
    @PostMapping("/test")
    public ResponseEntity<ThongBao> createTestNotification(
            Principal principal,
            @RequestBody Map<String, String> payload) {
        NguoiDung user = getUserFromPrincipal(principal);
        String tieuDe = payload.getOrDefault("tieuDe", "Thông báo test");
        String noiDung = payload.getOrDefault("noiDung", "Đây là thông báo test");
        String loai = payload.getOrDefault("loai", "INFO");
        
        ThongBao thongBao = thongBaoService.createNotification(user, tieuDe, noiDung, loai);
        return ResponseEntity.ok(thongBao);
    }

    // Helper method
    private NguoiDung getUserFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User not authenticated");
        }
        return nguoiDungRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
