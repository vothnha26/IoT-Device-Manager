package com.iot.management.service.impl;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.VaiTro;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.VaiTroRepository;
import com.iot.management.service.NguoiDungService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    public NguoiDungServiceImpl(NguoiDungRepository nguoiDungRepository,
                                VaiTroRepository vaiTroRepository,
                                PasswordEncoder passwordEncoder) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public NguoiDung registerUser(NguoiDung nguoiDung) {
        if (nguoiDungRepository.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            throw new RuntimeException("Lỗi: Tên đăng nhập đã tồn tại!");
        }
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        // Mã hóa mật khẩu
        nguoiDung.setMatKhauBam(passwordEncoder.encode(nguoiDung.getMatKhauBam()));

    // Tìm vai trò "ROLE_USER"
    VaiTro userRole = vaiTroRepository.findByName("ROLE_USER")
        .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy vai trò ROLE_USER."));

        // Gán vai trò cho người dùng
        Set<VaiTro> roles = new HashSet<>();
        roles.add(userRole);
        nguoiDung.setVaiTro(roles);

        // Lưu người dùng, JPA sẽ tự động cập nhật bảng PhanQuyen
        return nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public Optional<NguoiDung> findByEmail(String email) {
        return nguoiDungRepository.findByEmail(email);
    }

    @Override
    public Optional<NguoiDung> findById(Long id) {
        return nguoiDungRepository.findById(id);
    }

    @Override
    public NguoiDung save(NguoiDung nguoiDung) {
        // Ensure password field is already encoded if present - caller responsibility for raw password
        return nguoiDungRepository.save(nguoiDung);
    }

    @Override
    public void updatePassword(Long userId, String rawPassword) {
        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isPresent()) {
            NguoiDung u = opt.get();
            u.setMatKhauBam(passwordEncoder.encode(rawPassword));
            nguoiDungRepository.save(u);
        } else {
            throw new RuntimeException("Người dùng không tồn tại: " + userId);
        }
    }

    @Override
    public List<NguoiDung> findAllUsers() {
        return nguoiDungRepository.findAll();
    }
}