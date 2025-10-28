// package com.iot.management.config;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;
// import java.util.Collections;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import com.iot.management.model.entity.GoiCuoc;
// import com.iot.management.model.entity.KhuVuc;
// import com.iot.management.model.entity.LoaiThietBi;
// import com.iot.management.model.entity.NguoiDung;
// import com.iot.management.model.entity.ThietBi;
// import com.iot.management.model.entity.VaiTro;
// import com.iot.management.model.repository.GoiCuocRepository;
// import com.iot.management.model.repository.KhuVucRepository;
// import com.iot.management.model.repository.LoaiThietBiRepository;
// import com.iot.management.model.repository.NguoiDungRepository;
// import com.iot.management.model.repository.ThietBiRepository;
// import com.iot.management.model.repository.VaiTroRepository;
// import com.iot.management.model.entity.NhomThietBi;
// import org.springframework.context.annotation.Profile;

// @Component
// @Profile("dev")
// @Transactional
// public class DataSeeder implements CommandLineRunner {
//     private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

//     private final VaiTroRepository vaiTroRepository;
//     private final GoiCuocRepository goiCuocRepository;
//     private final NguoiDungRepository nguoiDungRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final LoaiThietBiRepository loaiThietBiRepository;
//     private final KhuVucRepository khuVucRepository;
//     private final ThietBiRepository thietBiRepository;

//     public DataSeeder(VaiTroRepository vaiTroRepository,
//                       GoiCuocRepository goiCuocRepository,
//                       NguoiDungRepository nguoiDungRepository,
//                       PasswordEncoder passwordEncoder,
//                       LoaiThietBiRepository loaiThietBiRepository,
//                       KhuVucRepository khuVucRepository,
//                       ThietBiRepository thietBiRepository) {
//         this.vaiTroRepository = vaiTroRepository;
//         this.goiCuocRepository = goiCuocRepository;
//         this.nguoiDungRepository = nguoiDungRepository;
//         this.passwordEncoder = passwordEncoder;
//         this.loaiThietBiRepository = loaiThietBiRepository;
//         this.khuVucRepository = khuVucRepository;
//         this.thietBiRepository = thietBiRepository;
//     }

//     @Override
//     @Transactional
//     public void run(String... args) throws Exception {
//         try {
//             // Seed roles and packages first
//             List<VaiTro> roles = seedVaiTro();
//             seedGoiCuoc();

//             // Seed admin and test users
//             seedAdminAccount();
//             seedTestUser(roles);

//             // Seed device types, locations and devices
//             seedDeviceTypes();
//             seedLocations();
//             seedDevices();
//             logger.info("Data seeding completed");
//         } catch (Exception e) {
//             logger.error("Error seeding data: ", e);
//             throw e;
//         }
//     }

//     private List<VaiTro> seedVaiTro() {
//         List<VaiTro> roles = new ArrayList<>();
//         if (vaiTroRepository.count() == 0) {
//             roles.add(createVaiTro("ROLE_USER", "Người dùng thông thường"));
//             roles.add(createVaiTro("ROLE_MANAGER", "Quản lý hệ thống"));
//             roles = vaiTroRepository.saveAll(roles);
//             logger.info("Roles seeded successfully");
//         } else {
//             roles = vaiTroRepository.findAll();
//         }
//         return roles;
//     }

//     private VaiTro createVaiTro(String tenVaiTro, String moTa) {
//         VaiTro role = new VaiTro();
//         role.setTenVaiTro(tenVaiTro);
//         return role;
//     }

//     private void seedGoiCuoc() {
//         // 1. Gói Free
//         createGoiCuocIfNotFound("Goi Free", new BigDecimal("0.00"), 5, 5, 7);
//         // 2. Gói Cá Nhân
//         createGoiCuocIfNotFound("Ca Nhan", new BigDecimal("200000.00"), 25, 20, 30);
//         // 3. Gói Pro
//         createGoiCuocIfNotFound("Goi Pro", new BigDecimal("500000.00"), 100, 75, 180);
//     }

//     private void createGoiCuocIfNotFound(String tenGoi, BigDecimal giaTien, int slThietBi, int slLuat, int soNgayLuu) {
//         Optional<GoiCuoc> existing = goiCuocRepository.findByTenGoi(tenGoi);
//         if (existing.isEmpty()) {
//             GoiCuoc g = new GoiCuoc();
//             g.setTenGoi(tenGoi);
//             g.setGiaTien(giaTien);
//             g.setSlThietBiToiDa(slThietBi);
//             g.setSlLuatToiDa(slLuat);
//             g.setSoNgayLuuDuLieu(soNgayLuu);
//             // defaults for new columns if required
//             g.setSlKhuVucToiDa(10);
//             g.setSlTokenToiDa(5);
//             goiCuocRepository.save(g);
//             logger.info("Seeded package: {}", tenGoi);
//         }
//     }

//     private void seedTestUser(List<VaiTro> roles) {
//         try {
//             String email = "test@example.com";
//             if (nguoiDungRepository.findByEmail(email).isEmpty()) {
//                 NguoiDung testUser = new NguoiDung();
//                 testUser.setTenDangNhap("test");
//                 testUser.setEmail(email);
//                 testUser.setMatKhauBam(passwordEncoder.encode("test123"));
//                 testUser.setKichHoat(true);
//                 testUser.setVaiTro(new HashSet<>(roles));
//                 nguoiDungRepository.save(testUser);
//                 logger.info("Test user seeded successfully");
//             }
//         } catch (Exception e) {
//             logger.error("Error seeding test user: ", e);
//             throw e;
//         }
//     }

//     private void seedDeviceTypes() {
//         try {
//             if (loaiThietBiRepository.count() == 0) {
//                 List<LoaiThietBi> deviceTypes = Arrays.asList(
//                     createDeviceType("Den LED RGB", "Den LED co the dieu chinh mau sac", NhomThietBi.CONTROLLER),
//                     createDeviceType("Cam bien nhiet do", "Thiet bi do nhiet do moi truong", NhomThietBi.SENSOR),
//                     createDeviceType("Cam bien do am", "Thiet bi do do am trong khong khi", NhomThietBi.SENSOR)
//                 );
//                 loaiThietBiRepository.saveAll(deviceTypes);
//                 logger.info("Device types seeded successfully");
//             } else {
//                 // Cập nhật nhóm thiết bị cho các loại đã có (nếu chưa có)
//                 updateDeviceTypeGroups();
//             }
//         } catch (Exception e) {
//             logger.error("Error seeding device types: ", e);
//             throw e;
//         }
//     }

//     private void updateDeviceTypeGroups() {
//         List<LoaiThietBi> allTypes = loaiThietBiRepository.findAll();
//         boolean updated = false;
        
//         for (LoaiThietBi type : allTypes) {
//             if (type.getNhomThietBi() == null) {
//                 String tenLoai = type.getTenLoai().toLowerCase();
                
//                 // Xác định nhóm dựa trên tên
//                 if (tenLoai.contains("đèn") || tenLoai.contains("led") || 
//                     tenLoai.contains("light") || tenLoai.contains("switch") || 
//                     tenLoai.contains("công tắc") || tenLoai.contains("relay")) {
//                     type.setNhomThietBi(NhomThietBi.CONTROLLER);
//                     updated = true;
//                 } else if (tenLoai.contains("cảm biến") || tenLoai.contains("sensor") ||
//                           tenLoai.contains("nhiệt độ") || tenLoai.contains("độ ẩm") ||
//                           tenLoai.contains("temperature") || tenLoai.contains("humidity")) {
//                     type.setNhomThietBi(NhomThietBi.SENSOR);
//                     updated = true;
//                 } else if (tenLoai.contains("motor") || tenLoai.contains("servo") ||
//                           tenLoai.contains("van") || tenLoai.contains("quạt") ||
//                           tenLoai.contains("fan")) {
//                     type.setNhomThietBi(NhomThietBi.ACTUATOR);
//                     updated = true;
//                 }
//             }
//         }
        
//         if (updated) {
//             loaiThietBiRepository.saveAll(allTypes);
//             logger.info("Device type groups updated successfully");
//         }
//     }

//     private LoaiThietBi createDeviceType(String name, String description, NhomThietBi nhomThietBi) {
//         LoaiThietBi t = new LoaiThietBi();
//         t.setTenLoai(name);
//         t.setMoTa(description);
//         t.setNhomThietBi(nhomThietBi);
//         return t;
//     }

//     private void seedLocations() {
//         try {
//             Optional<NguoiDung> maybe = nguoiDungRepository.findByEmail("test@example.com");
//             if (maybe.isEmpty()) return;
//             NguoiDung testUser = maybe.get();

//             if (khuVucRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung()).isEmpty()) {
//                 KhuVuc home = new KhuVuc();
//                 home.setChuSoHuu(testUser);
//                 home.setTenKhuVuc("Nha cua toi");
//                 home.setLoaiKhuVuc("can ho");
//                 home = khuVucRepository.save(home);

//                 List<KhuVuc> rooms = Arrays.asList(
//                     createRoom(testUser, home, "Phong khach", "phong"),
//                     createRoom(testUser, home, "Phong ngu", "phong")
//                 );
//                 khuVucRepository.saveAll(rooms);
//                 logger.info("Locations seeded successfully");
//             }
//         } catch (Exception e) {
//             logger.error("Error seeding locations: ", e);
//             throw e;
//         }
//     }

//     private KhuVuc createRoom(NguoiDung owner, KhuVuc parent, String name, String type) {
//         KhuVuc k = new KhuVuc();
//         k.setChuSoHuu(owner);
//         k.setKhuVucCha(parent);
//         k.setTenKhuVuc(name);
//         k.setLoaiKhuVuc(type);
//         return k;
//     }

//     private void seedDevices() {
//         try {
//             Optional<NguoiDung> maybe = nguoiDungRepository.findByEmail("test@example.com");
//             if (maybe.isEmpty()) return;
//             NguoiDung testUser = maybe.get();

//             if (thietBiRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung()).isEmpty()) {
//                 List<KhuVuc> locations = khuVucRepository.findByChuSoHuu_MaNguoiDung(testUser.getMaNguoiDung());
//                 List<LoaiThietBi> deviceTypes = loaiThietBiRepository.findAll();
//                 if (!locations.isEmpty() && !deviceTypes.isEmpty()) {
//                     List<ThietBi> devices = new ArrayList<>();
//                     for (KhuVuc location : locations) {
//                         if (location.getKhuVucCha() != null) continue; // keep simple
//                         devices.add(createDevice(testUser, location, deviceTypes.get(0), "Den " + location.getTenKhuVuc()));
//                     }
//                     thietBiRepository.saveAll(devices);
//                     logger.info("Devices seeded successfully");
//                 }
//             }
//         } catch (Exception e) {
//             logger.error("Error seeding devices: ", e);
//             throw e;
//         }
//     }

//     private ThietBi createDevice(NguoiDung owner, KhuVuc location, LoaiThietBi type, String name) {
//         ThietBi device = new ThietBi();
//         device.setChuSoHuu(owner);
//         device.setKhuVuc(location);
//         device.setLoaiThietBi(type);
//         device.setTenThietBi(name);
//         device.setTokenThietBi(UUID.randomUUID().toString());
//         device.setTrangThai("hoat dong");
//         device.setLanHoatDongCuoi(LocalDateTime.now());
//         device.setNgayLapDat(LocalDate.now());
//         return device;
//     }
    
// private void seedAdminAccount() {
//         String adminEmail = "admin@system.com";
//         if (nguoiDungRepository.findByEmail(adminEmail).isPresent()) {
//             logger.info("Admin account already exists");
//             return;
//         }

//         Optional<VaiTro> managerRole = vaiTroRepository.findByName("ROLE_MANAGER");
//         if (managerRole.isEmpty()) {
//             logger.error("Could not create admin account: ROLE_MANAGER not found");
//             return;
//         }

//         NguoiDung admin = new NguoiDung();
//         admin.setEmail(adminEmail);
//         admin.setTenDangNhap("admin");
//         admin.setMatKhauBam(passwordEncoder.encode("Admin@123"));
//         admin.setKichHoat(true);
//         admin.setVaiTro(new HashSet<>(Collections.singletonList(managerRole.get())));
//         nguoiDungRepository.save(admin);
//         logger.info("Created admin account with email: {}", adminEmail);
//     }
// }