package com.iot.management.controller.api;

import com.iot.management.model.entity.LichSuCanhBao;
import com.iot.management.model.repository.LichSuCanhBaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lich-su-canh-bao")
public class LichSuCanhBaoController {

    private final LichSuCanhBaoRepository lichSuCanhBaoRepository;

    public LichSuCanhBaoController(LichSuCanhBaoRepository lichSuCanhBaoRepository) {
        this.lichSuCanhBaoRepository = lichSuCanhBaoRepository;
    }

    /**
     * Lấy lịch sử cảnh báo của một luật (có phân trang và filter theo thời gian)
     */
    @GetMapping("/luat/{maLuat}")
    public ResponseEntity<?> getLichSuByLuat(
            @PathVariable Long maLuat,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "all") String filter) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<LichSuCanhBao> lichSuPage = lichSuCanhBaoRepository
                    .findByLuat_MaLuatOrderByThoiGianDesc(maLuat, pageable);
            
            // Filter by time range
            LocalDateTime startTime = getStartTimeByFilter(filter);
            List<LichSuCanhBao> filteredContent = lichSuPage.getContent();
            
            if (startTime != null) {
                filteredContent = filteredContent.stream()
                        .filter(log -> log.getThoiGian().isAfter(startTime))
                        .collect(Collectors.toList());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", filteredContent);
            response.put("totalElements", filter.equals("all") ? lichSuPage.getTotalElements() : filteredContent.size());
            response.put("totalPages", lichSuPage.getTotalPages());
            response.put("currentPage", lichSuPage.getNumber());
            response.put("size", lichSuPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Không thể lấy lịch sử: " + e.getMessage()));
        }
    }

    /**
     * Đếm số lượng log của luật
     */
    @GetMapping("/luat/{maLuat}/count")
    public ResponseEntity<?> countByLuat(@PathVariable Long maLuat) {
        try {
            Long count = lichSuCanhBaoRepository.countByLuat_MaLuat(maLuat);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Không thể đếm: " + e.getMessage()));
        }
    }
    
    /**
     * Helper method to get start time based on filter
     */
    private LocalDateTime getStartTimeByFilter(String filter) {
        LocalDate today = LocalDate.now();
        switch (filter) {
            case "today":
                return LocalDateTime.of(today, LocalTime.MIN);
            case "yesterday":
                return LocalDateTime.of(today.minusDays(1), LocalTime.MIN);
            case "week":
                return LocalDateTime.of(today.minusDays(7), LocalTime.MIN);
            case "month":
                return LocalDateTime.of(today.minusDays(30), LocalTime.MIN);
            default:
                return null; // "all" - no filter
        }
    }
}
