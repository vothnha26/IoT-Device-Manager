package com.iot.management.service;

import java.math.BigDecimal;

public interface VietQRService {
    /**
     * Generate VietQR payment image URL for an order
     */
    String generateQRCode(Integer orderId, BigDecimal amount, String description);
}
