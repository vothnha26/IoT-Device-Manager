package com.iot.management.config;

import org.springframework.context.annotation.Configuration;

/**
 * Deprecated placeholder for VNPAY configuration. VNPAY integration has been removed.
 * This class remains to avoid breaking references in older compiled artifacts but returns
 * null/defaults. Prefer SePayProperties / VietQRProperties for payment configuration.
 */
@Deprecated
@Configuration
public class VnpayConfig {
    public String getTmnCode() { return null; }
    public String getHashSecret() { return null; }
    public String getUrl() { return null; }
    public String getReturnUrl() { return null; }
    public static String getResponseDescription(String code) { return null; }
}