package com.iot.management.config;

import com.iot.management.model.entity.*;
import com.iot.management.model.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.*;

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final KhuVucRepository khuVucRepository;
    private final ThietBiRepository thietBiRepository;

    public DataSeeder(VaiTroRepository vaiTroRepository, 
                     NguoiDungRepository nguoiDungRepository,
                     PasswordEncoder passwordEncoder,
                     LoaiThietBiRepository loaiThietBiRepository,
                     KhuVucRepository khuVucRepository,
                     ThietBiRepository thietBiRepository) {
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordEncoder = passwordEncoder;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.khuVucRepository = khuVucRepository;
        this.thietBiRepository = thietBiRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // 1. Seed basic roles
            List<VaiTro> roles = seedVaiTro();
            VaiTro roleUser = roles.stream()
                .filter(r -> r.getTenVaiTro().equals("ROLE_USER"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER not found"));

            VaiTro roleManager = roles.stream()
                .filter(r -> r.getTenVaiTro().equals("ROLE_MANAGER"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role ROLE_MANAGER not found"));
            
            // 2. Seed admin and test user accounts
            seedAdminAccount();
            seedTestUser(Arrays.asList(roleUser, roleManager));

            // 3. Seed device types
            seedDeviceTypes();

            // 4. Seed locations for test user
            seedLocations();

            // 5. Seed devices for test user
            seedDevices();

            logger.info("Basic data seeding completed");
        } catch (Exception e) {
            logger.error("Error seeding data: ", e);
            throw e;
        }
    }

    private List<VaiTro> seedVaiTro() {
        List<VaiTro> roles = new ArrayList<>();
        if (vaiTroRepository.count() == 0) {
            // Create basic roles
            roles.add(createVaiTro("ROLE_USER", "Người dùng thông thường"));
            roles.add(createVaiTro("ROLE_MANAGER", "Quản lý hệ thống"));
            roles = vaiTroRepository.saveAll(roles);
            logger.info("Roles seeded successfully");
        } else {
            roles = vaiTroRepository.findAll();
        }
        return roles;
    }

    private VaiTro createVaiTro(String tenVaiTro, String moTa) {
        VaiTro role = new VaiTro();
        role.setTenVaiTro(tenVaiTro);
        return role;
    }

    private void seedTestUser(List<VaiTro> roles) {
        try {
            // Kiểm tra xem người dùng test đã tồn tại chưa
            String email = "test@example.com";
            if (nguoiDungRepository.findByEmail(email).isEmpty()) {
                // Tạo người dùng test với vai trò USER và MANAGER
                NguoiDung testUser = new NguoiDung();
                testUser.setTenDangNhap("test");
                testUser.setEmail(email); 
                testUser.setMatKhauBam(passwordEncoder.encode("test123"));
                testUser.setKichHoat(true);
                testUser.setVaiTro(new HashSet<>(roles));

                nguoiDungRepository.save(testUser);
                logger.info("Test user seeded successfully");
            }
        } catch (Exception e) {
            logger.error("Error seeding test user: ", e);
            throw e;
        }
    }

    private void seedDeviceTypes() {
        try {
            // Tạo loại thiết bị mẫu nếu chưa có
            if (loaiThietBiRepository.count() == 0) {
                List<LoaiThietBi> deviceTypes = Arrays.asList(
                    createDeviceType("Đèn LED RGB", "Đèn LED có thể điều chỉnh màu sắc"),
                    createDeviceType("Cảm biến nhiệt độ", "Thiết bị đo nhiệt độ môi trường"),
                    createDeviceType("Cảm biến độ ẩm", "Thiết bị đo độ ẩm trong không khí"),
                    createDeviceType("Rèm tự động", "Thiết bị điều khiển rèm cửa"),
                    createDeviceType("Công tắc thông minh", "Công tắc điều khiển từ xa")
                );
                loaiThietBiRepository.saveAll(deviceTypes);
                logger.info("Device types seeded successfully");
            }
        } catch (Exception e) {
            logger.error("Error seeding device types: ", e);
            throw e;
        }
    }

    private LoaiThietBi createDeviceType(String name, String description) {
        LoaiThietBi type = new LoaiThietBi();
        type.setTenLoai(name);
        type.setMoTa(description);
        return type;
    }

    private void seedLocations() {
        try {
            // Chỉ tạo khu vực mẫu cho test user
            NguoiDung testUser = nguoiDungRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("Test user not found"));

            if (khuVucRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung()).isEmpty()) {
                // Tạo khu vực cha
                KhuVuc home = new KhuVuc();
                home.setChuSoHuu(testUser);
                home.setTenKhuVuc("Nhà của tôi");
                home.setLoaiKhuVuc("căn hộ");
                home = khuVucRepository.save(home);

                // Tạo các khu vực con
                List<KhuVuc> rooms = Arrays.asList(
                    createRoom(testUser, home, "Phòng khách", "phòng"),
                    createRoom(testUser, home, "Phòng ngủ", "phòng"),
                    createRoom(testUser, home, "Nhà bếp", "phòng"),
                    createRoom(testUser, home, "Ban công", "ban công")
                );
                khuVucRepository.saveAll(rooms);
                logger.info("Locations seeded successfully");
            }
        } catch (Exception e) {
            logger.error("Error seeding locations: ", e);
            throw e;
        }
    }

    private KhuVuc createRoom(NguoiDung owner, KhuVuc parent, String name, String type) {
        KhuVuc room = new KhuVuc();
        room.setChuSoHuu(owner);
        room.setKhuVucCha(parent);
        room.setTenKhuVuc(name);
        room.setLoaiKhuVuc(type);
        return room;
    }

    private void seedDevices() {
        try {
            NguoiDung testUser = nguoiDungRepository.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("Test user not found"));

            if (thietBiRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung()).isEmpty()) {
                // Lấy danh sách khu vực và loại thiết bị
                List<KhuVuc> locations = khuVucRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung());
                List<LoaiThietBi> deviceTypes = loaiThietBiRepository.findAll();

                if (!locations.isEmpty() && !deviceTypes.isEmpty()) {
                    // Tạo thiết bị mẫu cho mỗi khu vực
                    List<ThietBi> devices = new ArrayList<>();
                    
                    for (KhuVuc location : locations) {
                        if (location.getKhuVucCha() != null) { // Chỉ thêm thiết bị vào các phòng (không phải khu vực cha)
                            devices.addAll(Arrays.asList(
                                createDevice(testUser, location, deviceTypes.get(0), "Đèn " + location.getTenKhuVuc()),
                                createDevice(testUser, location, deviceTypes.get(1), "Nhiệt độ " + location.getTenKhuVuc()),
                                createDevice(testUser, location, deviceTypes.get(2), "Độ ẩm " + location.getTenKhuVuc())
                            ));
                        }
                    }
                    
                    thietBiRepository.saveAll(devices);
                    logger.info("Devices seeded successfully");
                }
            }
        } catch (Exception e) {
            logger.error("Error seeding devices: ", e);
            throw e;
        }
    }

    private ThietBi createDevice(NguoiDung owner, KhuVuc location, LoaiThietBi type, String name) {
        ThietBi device = new ThietBi();
        device.setChuSoHuu(owner);
        device.setKhuVuc(location);
        device.setLoaiThietBi(type);
        device.setTenThietBi(name);
        device.setTokenThietBi(UUID.randomUUID().toString());
        device.setTrangThai("hoạt động");
        device.setLanHoatDongCuoi(LocalDateTime.now());
        device.setNgayLapDat(LocalDate.now());
        return device;
    }

    private void seedAdminAccount() {
        String adminEmail = "admin@system.com";
        
        // Return if admin exists
        if (nguoiDungRepository.findByEmail(adminEmail).isPresent()) {
            logger.info("Admin account already exists");
            return;
        }

        // Get MANAGER role
        Optional<VaiTro> managerRole = vaiTroRepository.findByName("ROLE_MANAGER");
        if (managerRole.isEmpty()) {
            logger.error("Could not create admin account: ROLE_MANAGER not found");
            return;
        }

        // Create admin account
        NguoiDung admin = new NguoiDung();
        admin.setEmail(adminEmail);
        admin.setTenDangNhap("admin");
        admin.setMatKhauBam(passwordEncoder.encode("Admin@123")); 
        admin.setKichHoat(true);
        admin.setVaiTro(new HashSet<>(Collections.singletonList(managerRole.get())));
        
        nguoiDungRepository.save(admin);
        logger.info("Created admin account with email: {}", adminEmail);
    }
}