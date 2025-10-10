package com.iot.management.config;

import com.iot.management.model.entity.GoiCuoc;
import com.iot.management.model.entity.VaiTro;
import com.iot.management.model.repository.GoiCuocRepository;
import com.iot.management.model.repository.VaiTroRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final VaiTroRepository vaiTroRepository;
    private final GoiCuocRepository goiCuocRepository;

    // Cập nhật constructor để inject GoiCuocRepository
    public DataSeeder(VaiTroRepository vaiTroRepository, GoiCuocRepository goiCuocRepository) {
        this.vaiTroRepository = vaiTroRepository;
        this.goiCuocRepository = goiCuocRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedGoiCuoc(); // Gọi phương thức thêm gói cước
    }

    private void seedRoles() {
        List<String> roleNames = Arrays.asList("USER", "MANAGER");
        for (String roleName : roleNames) {
            Optional<VaiTro> existingRole = vaiTroRepository.findByName(roleName);
            if (existingRole.isEmpty()) {
                VaiTro newRole = new VaiTro();
                newRole.setName(roleName);
                vaiTroRepository.save(newRole);
                System.out.println("✅ Seeded role: " + roleName);
            } else {
                System.out.println("⏩ Role already exists: " + roleName);
            }
        }
    }

    // Phương thức mới để thêm các gói cước
    private void seedGoiCuoc() {
        // 1. Gói Free - tránh ký tự đặc biệt
        createGoiCuocIfNotFound("Gói Free", new BigDecimal("0.00"), 5, 5, 7);

        // 2. Gói Cá Nhân - UTF-8 OK 
        createGoiCuocIfNotFound("Cá Nhân", new BigDecimal("5.00"), 25, 20, 30);

        // 3. Gói Pro - tránh ký tự đặc biệt
        createGoiCuocIfNotFound("Gói Pro", new BigDecimal("20.00"), 100, 75, 180);
    }

    // Phương thức trợ giúp để tạo gói cước nếu chưa tồn tại
    private void createGoiCuocIfNotFound(String tenGoi, BigDecimal giaTien, int slThietBi, int slLuat, int soNgayLuu) {
        Optional<GoiCuoc> existingPackage = goiCuocRepository.findByTenGoi(tenGoi);
        if (existingPackage.isEmpty()) {
            GoiCuoc goiCuoc = new GoiCuoc();
            goiCuoc.setTenGoi(tenGoi);
            goiCuoc.setGiaTien(giaTien);
            goiCuoc.setSlThietBiToiDa(slThietBi);
            goiCuoc.setSlLuatToiDa(slLuat);
            goiCuoc.setSoNgayLuuDuLieu(soNgayLuu);
            goiCuocRepository.save(goiCuoc);
            System.out.println("✅ Seeded package: " + tenGoi);
        } else {
            System.out.println("⏩ Package already exists: " + tenGoi);
        }
    }
}