package com.iot.management;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotManagementApplication {

    static {
        // Tải biến môi trường từ file .env nếu có; nếu không có thì bỏ qua (không ném lỗi)
        try {
            Dotenv dotenv = Dotenv.load();
            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception e) {
            // Không tìm thấy .env hoặc lỗi khi đọc -> tiếp tục với các System properties hiện có
            System.out.println("Warning: .env not found or could not be loaded. Continuing without it.");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(IotManagementApplication.class, args);
    }
}