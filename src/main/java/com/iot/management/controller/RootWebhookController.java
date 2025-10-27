package com.iot.management.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.iot.management.service.SePayService;

/**
 * Fallback webhook controller at root "/" to catch SePay webhooks sent to incorrect URL.
 * SePay may send webhooks to the root domain if callback_url is not properly configured.
 */
@RestController
public class RootWebhookController {

    private static final Logger log = LoggerFactory.getLogger(RootWebhookController.class);

    private final SePayService sePayService;

    public RootWebhookController(SePayService sePayService) {
        this.sePayService = sePayService;
    }

    @PostMapping("/")
    public ResponseEntity<?> handleRootWebhook(@RequestBody Map<String, Object> payload,
                                               @RequestHeader(value = "X-SEPAY-SIGN", required = false) String signature) {
        log.warn("========================================");
        log.warn("⚠️ Received SePay webhook at ROOT '/' instead of '/api/payments/webhook'");
        log.warn("Check SePay callback URL configuration!");
        log.warn("========================================");
        log.info("ROOT webhook payload: {}", payload);
        log.info("ROOT webhook signature: {}", signature);
        
        // Process webhook using the same service
        processWebhookAsync(payload, signature);
        
        log.info("✅ Returning 200 OK response to SePay");
        return ResponseEntity.ok(Map.of("status", "accepted", "message", "webhook received at root"));
    }

    private void processWebhookAsync(Map<String, Object> payload, String signature) {
        log.info("🚀 Starting async webhook processing...");
        try {
            new Thread(() -> {
                try {
                    log.info("⚙️ Webhook thread started, calling SePayService.handleWebhook()");
                    sePayService.handleWebhook(payload, signature);
                    log.info("✅ Webhook processing completed successfully");
                } catch (Exception e) {
                    log.error("❌ Error handling ROOT webhook async: {}", e.getMessage(), e);
                    e.printStackTrace();
                }
            }, "sepay-root-webhook-processor").start();
            log.info("✅ Async thread spawned successfully");
        } catch (Exception ex) {
            log.error("❌ Failed to start async ROOT webhook processor: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
}
