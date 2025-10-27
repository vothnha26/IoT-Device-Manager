package com.iot.management.controller.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.service.SePayService;
import com.iot.management.model.repository.ThanhToanRepository;
import com.iot.management.model.entity.ThanhToan;
import com.iot.management.model.entity.DangKyGoi;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class SePayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SePayWebhookController.class);

    private final SePayService sePayService;
    private final ThanhToanRepository thanhToanRepository;

    public SePayWebhookController(SePayService sePayService, ThanhToanRepository thanhToanRepository) {
        this.sePayService = sePayService;
        this.thanhToanRepository = thanhToanRepository;
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload,
                                     @RequestHeader(value = "X-SEPAY-SIGN", required = false) String signature) {
        log.info("Received SePay webhook at /api/payments/webhook (async accepted), signature={}", signature);
        processWebhookAsync(payload, signature);
        return ResponseEntity.ok(Map.of("status", "accepted"));
    }

    /**
     * Fallback webhook endpoint at root "/" in case SePay misconfigured or sends to root.
     * This should be registered explicitly in HomeController or a separate controller.
     */
    private void processWebhookAsync(Map<String, Object> payload, String signature) {
        try {
            new Thread(() -> {
                try {
                    sePayService.handleWebhook(payload, signature);
                } catch (Exception e) {
                    log.error("Error handling SePay webhook async: {}", e.getMessage(), e);
                }
            }, "sepay-webhook-processor").start();
        } catch (Exception ex) {
            log.error("Failed to start async webhook processor: {}", ex.getMessage(), ex);
        }
    }

    // Lightweight polling endpoint for frontend to check payment/package status
    @GetMapping("/status/{orderId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable("orderId") Long orderId) {
        Optional<ThanhToan> opt = thanhToanRepository.findById(orderId);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "status", "NOT_FOUND"
            ));
        }

        ThanhToan tt = opt.get();
        String status = tt.getTrangThai();
        String packageStatus = null;
        boolean active = false;

        if (tt.getDangKyGoi() != null) {
            DangKyGoi dk = tt.getDangKyGoi();
            packageStatus = dk.getTrangThai();
            active = DangKyGoi.TRANG_THAI_ACTIVE.equals(packageStatus)
                    && dk.getNgayKetThuc() != null
                    && dk.getNgayKetThuc().isAfter(LocalDateTime.now());
        }

        return ResponseEntity.ok(Map.of(
            "status", status,
            "packageStatus", packageStatus,
            "active", active,
            "redirect", active ? "/profile" : null
        ));
    }
}
