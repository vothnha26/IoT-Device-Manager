package com.iot.management.service;

import com.iot.management.model.entity.NguoiDung;
import java.util.Optional;

public interface NguoiDungService {
    // Phương thức để đăng ký người dùng mới
    NguoiDung registerUser(NguoiDung nguoiDung);

    // Tìm người dùng theo email (thường dùng cho việc đăng nhập và kiểm tra)
    Optional<NguoiDung> findByEmail(String email);

    // Lấy thông tin người dùng theo ID
    Optional<NguoiDung> findById(Long id);

    // Lưu hoặc cập nhật người dùng (dùng cho cập nhật thông tin profile)
    NguoiDung save(NguoiDung nguoiDung);

    // Cập nhật mật khẩu cho người dùng (raw password -> encode)
    void updatePassword(Long userId, String rawPassword);
}