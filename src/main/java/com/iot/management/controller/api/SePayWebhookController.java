package com.iot.management.controller.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.service.SePayService;

@RestController
@RequestMapping("/api/payments")
public class SePayWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SePayWebhookController.class);

    private final SePayService sePayService;

    public SePayWebhookController(SePayService sePayService) {
        this.sePayService = sePayService;
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
}
