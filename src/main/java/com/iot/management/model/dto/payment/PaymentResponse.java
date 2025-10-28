// Tệp: PaymentResponse.java
package com.iot.management.model.dto.payment;

// Loại bỏ @Builder và @Data, thay bằng Constructor và Getters/Setters

public class PaymentResponse {
    private String orderCode;      
    private String paymentUrl;     
    private String description;    
    private Long amount;           
    private String qrCodeData;     

    // Constructor đầy đủ (Thay thế cho @Builder)
    public PaymentResponse(String orderCode, String paymentUrl, String description, Long amount, String qrCodeData) {
        this.orderCode = orderCode;
        this.paymentUrl = paymentUrl;
        this.description = description;
        this.amount = amount;
        this.qrCodeData = qrCodeData;
    }
    
    // Constructor mặc định (cần thiết cho Spring)
    public PaymentResponse() {
    }

    // Getters và Setters
    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
}