package com.iot.management.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for SePay integration (create checkout URL, handle webhook).
 */
public interface SePayService {
    /**
     * Create a checkout URL (SePay hosted) for a given order/registration
     */
    String createPaymentUrl(Integer orderId, BigDecimal amount, String description);

    /**
     * Handle incoming SePay webhook payload. Signature header may be used for validation.
     */
    void handleWebhook(Map<String, Object> payload, String signatureHeader);
}
