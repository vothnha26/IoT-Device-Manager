package com.iot.management.controller.api.test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HashPass {
    public static void main(String[] args) {
        // 1. Tạo một instance của BCrypt
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        
        // 2. Mật khẩu thô bạn muốn hash
        String rawPassword = "admin123";
        
        // 3. Mã hóa nó
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        // 4. In ra chuỗi hash
        // Mỗi lần chạy, chuỗi này sẽ KHÁC NHAU, nhưng tất cả đều hợp lệ
        System.out.println(hashedPassword);
    }
}

