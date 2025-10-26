package com.iot.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SePayProperties {

    @Value("${sepay.apiKey:}")
    private String apiKey;

    @Value("${sepay.secret:}")
    private String secret;

    @Value("${sepay.apiBaseUrl:https://api.sepay.vn/}")
    private String apiBaseUrl;

    @Value("${sepay.publicBaseUrl:http://localhost:8080/}")
    private String publicBaseUrl;

    @Value("${sepay.callbackPath:/api/payments/webhook}")
    private String callbackPath;

    @Value("${sepay.returnUrl:http://localhost:8080/}")
    private String returnUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getSecret() {
        return secret;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public String getCallbackPath() {
        return callbackPath;
    }

    public String getReturnUrl() {
        return returnUrl;
    }
}