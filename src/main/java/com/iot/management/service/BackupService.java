package com.iot.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iot.management.model.dto.backup.*;
import com.iot.management.model.entity.*;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.model.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BackupService {
    
    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);
    
    private final NguoiDungRepository nguoiDungRepository;
    private final DuAnRepository duAnRepository;
    private final KhuVucRepository khuVucRepository;
    private final ThietBiRepository thietBiRepository;
    private final PhanQuyenDuAnRepository phanQuyenDuAnRepository;
    private final LoaiThietBiRepository loaiThietBiRepository;
    private final ObjectMapper objectMapper;

    public BackupService(NguoiDungRepository nguoiDungRepository,
                        DuAnRepository duAnRepository,
                        KhuVucRepository khuVucRepository,
                        ThietBiRepository thietBiRepository,
                        PhanQuyenDuAnRepository phanQuyenDuAnRepository,
                        LoaiThietBiRepository loaiThietBiRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.duAnRepository = duAnRepository;
        this.khuVucRepository = khuVucRepository;
        this.thietBiRepository = thietBiRepository;
        this.phanQuyenDuAnRepository = phanQuyenDuAnRepository;
        this.loaiThietBiRepository = loaiThietBiRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Export dữ liệu dự án của user ra JSON
     */
    @Transactional(readOnly = true)
    public String exportUserData(String email) throws IOException {
        logger.info("Exporting data for user: {}", email);
        
        NguoiDung user = nguoiDungRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Tạo backup data
        BackupDataDTO backupData = new BackupDataDTO();
        backupData.setUserInfo(new BackupDataDTO.UserInfoDTO(
            user.getTenDangNhap(),
            user.getEmail(),
            user.getTenDangNhap() // Dùng tenDangNhap thay vì hoTen
        ));
        backupData.setBackupDate(LocalDateTime.now());

        // Export tất cả dự án của user
        List<DuAn> duAns = duAnRepository.findByNguoiDung(user);
        logger.info("Found {} projects for user {}", duAns.size(), email);
        List<DuAnBackupDTO> duAnBackups = new ArrayList<>();

        for (DuAn duAn : duAns) {
            logger.info("Processing project: {} (ID: {})", duAn.getTenDuAn(), duAn.getMaDuAn());
            DuAnBackupDTO duAnBackup = new DuAnBackupDTO();
            duAnBackup.setTenDuAn(duAn.getTenDuAn());
            duAnBackup.setMoTa(duAn.getMoTa());
            duAnBackup.setNgayTao(duAn.getNgayTao());

            // Export khu vực (từ getKhuVucs() của DuAn)
            List<KhuVucBackupDTO> khuVucBackups = new ArrayList<>();
            if (duAn.getKhuVucs() != null) {
                logger.info("  Found {} zones in project {}", duAn.getKhuVucs().size(), duAn.getTenDuAn());
                khuVucBackups = duAn.getKhuVucs().stream()
                    .map(kv -> {
                        KhuVucBackupDTO dto = new KhuVucBackupDTO();
                        dto.setTenKhuVuc(kv.getTenKhuVuc());
                        dto.setMoTa(kv.getMoTa());
                        dto.setViTri(""); // Không có field viTri
                        dto.setNgayTao(LocalDateTime.now()); // Không có field ngayTao
                        return dto;
                    })
                    .collect(Collectors.toList());
            }
            duAnBackup.setKhuVucs(khuVucBackups);

            // Export thiết bị từ tất cả khu vực
            List<ThietBiBackupDTO> thietBiBackups = new ArrayList<>();
            if (duAn.getKhuVucs() != null) {
                for (KhuVuc khuVuc : duAn.getKhuVucs()) {
                    List<ThietBi> thietBis = thietBiRepository.findByKhuVuc_MaKhuVuc(khuVuc.getMaKhuVuc());
                    if (thietBis != null && !thietBis.isEmpty()) {
                        logger.info("  Found {} devices in zone {}", thietBis.size(), khuVuc.getTenKhuVuc());
                        for (ThietBi tb : thietBis) {
                            ThietBiBackupDTO dto = new ThietBiBackupDTO();
                            dto.setTenThietBi(tb.getTenThietBi());
                            dto.setMoTa(tb.getMoTa());
                            dto.setLoaiThietBi(tb.getLoaiThietBi() != null ? tb.getLoaiThietBi().getTenLoai() : "");
                            dto.setKhuVucTen(khuVuc.getTenKhuVuc());
                            dto.setTrangThai(tb.getTrangThai());
                            dto.setNgayLapDat(tb.getNgayLapDat());
                            dto.setLanHoatDongCuoi(tb.getLanHoatDongCuoi());
                            thietBiBackups.add(dto);
                        }
                    }
                }
            }
            duAnBackup.setThietBis(thietBiBackups);

            // Đơn giản hóa: Bỏ luật vì cấu trúc phức tạp
            duAnBackup.setLuats(new ArrayList<>());

            duAnBackups.add(duAnBackup);
        }

        backupData.setDuAn(duAnBackups);

        // Convert to JSON
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(backupData);
        logger.info("Export completed. Data size: {} bytes, projects: {}", json.length(), duAnBackups.size());
        
        return json;
    }

    /**
     * Import dữ liệu từ file backup
     */
    @Transactional(timeout = 300) // Timeout 5 phút
    public synchronized void importUserData(String email, MultipartFile file) throws IOException {
        logger.info("=== IMPORT START: User={}, Thread={} ===", email, Thread.currentThread().getName());
        logger.info("Importing data for user: {}", email);
        
        NguoiDung user = nguoiDungRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra xem user có gói cước active không
        boolean hasActivePackage = user.getDangKyGois() != null && 
            user.getDangKyGois().stream()
                .anyMatch(dkg -> "ACTIVE".equals(dkg.getTrangThai()));
        
        if (!hasActivePackage) {
            throw new RuntimeException("Bạn cần đăng ký gói cước trước khi khôi phục dữ liệu");
        }

        // Parse JSON
        BackupDataDTO backupData = objectMapper.readValue(file.getBytes(), BackupDataDTO.class);
        logger.info("Parsed backup data: {} projects", backupData.getDuAn() != null ? backupData.getDuAn().size() : 0);
        
        if (backupData.getDuAn() == null || backupData.getDuAn().isEmpty()) {
            throw new RuntimeException("No project data found in backup file");
        }

        // Import từng dự án
        int restoredCount = 0;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        
        for (DuAnBackupDTO duAnBackup : backupData.getDuAn()) {
            logger.info("Restoring project: {}", duAnBackup.getTenDuAn());
            
            // Tạo dự án mới với timestamp để tránh trùng lặp
            DuAn duAn = new DuAn();
            duAn.setTenDuAn(duAnBackup.getTenDuAn() + " (Restored " + timestamp + ")");
            duAn.setMoTa(duAnBackup.getMoTa());
            duAn.setNgayTao(LocalDateTime.now());
            duAn.setNguoiDung(user);
            duAn = duAnRepository.save(duAn);
            logger.info("Project saved with ID: {}", duAn.getMaDuAn());

            // Tạo phân quyền CHỦ SỞ HỮU cho user
            PhanQuyenDuAn phanQuyen = new PhanQuyenDuAn();
            phanQuyen.setDuAn(duAn);
            phanQuyen.setNguoiDung(user);
            phanQuyen.setVaiTro(DuAnRole.CHU_SO_HUU);
            phanQuyen.setNgayCapQuyen(LocalDateTime.now());
            phanQuyenDuAnRepository.save(phanQuyen);
            logger.info("Permission CHU_SO_HUU granted to user for project {}", duAn.getTenDuAn());

            // Import khu vực
            Map<String, KhuVuc> khuVucMap = new HashMap<>(); // Map tên khu vực -> KhuVuc object
            if (duAnBackup.getKhuVucs() != null && !duAnBackup.getKhuVucs().isEmpty()) {
                logger.info("Restoring {} zones for project {}", duAnBackup.getKhuVucs().size(), duAn.getTenDuAn());
                for (KhuVucBackupDTO kvBackup : duAnBackup.getKhuVucs()) {
                    KhuVuc khuVuc = new KhuVuc();
                    khuVuc.setTenKhuVuc(kvBackup.getTenKhuVuc());
                    khuVuc.setMoTa(kvBackup.getMoTa());
                    khuVuc.setDuAn(duAn);
                    khuVuc.setChuSoHuu(user); // Set chủ sở hữu cho khu vực
                    khuVuc = khuVucRepository.save(khuVuc);
                    logger.info("Zone saved: {} (ID: {})", khuVuc.getTenKhuVuc(), khuVuc.getMaKhuVuc());
                    khuVucMap.put(kvBackup.getTenKhuVuc(), khuVuc);
                }
            } else {
                logger.info("No zones to restore for project {}", duAn.getTenDuAn());
            }

            // Import thiết bị
            if (duAnBackup.getThietBis() != null && !duAnBackup.getThietBis().isEmpty()) {
                logger.info("Restoring {} devices for project {}", duAnBackup.getThietBis().size(), duAn.getTenDuAn());
                for (ThietBiBackupDTO tbBackup : duAnBackup.getThietBis()) {
                    // Tìm khu vực tương ứng
                    KhuVuc khuVuc = khuVucMap.get(tbBackup.getKhuVucTen());
                    if (khuVuc != null) {
                        ThietBi thietBi = new ThietBi();
                        thietBi.setTenThietBi(tbBackup.getTenThietBi());
                        thietBi.setMoTa(tbBackup.getMoTa());
                        thietBi.setKhuVuc(khuVuc);
                        thietBi.setChuSoHuu(user);
                        thietBi.setTrangThai(tbBackup.getTrangThai());
                        thietBi.setNgayLapDat(tbBackup.getNgayLapDat());
                        thietBi.setLanHoatDongCuoi(tbBackup.getLanHoatDongCuoi());
                        
                        // Generate token mới cho thiết bị (không restore token cũ)
                        thietBi.setTokenThietBi(java.util.UUID.randomUUID().toString());
                        
                        // Tìm loại thiết bị từ DB
                        if (tbBackup.getLoaiThietBi() != null && !tbBackup.getLoaiThietBi().isEmpty()) {
                            LoaiThietBi loaiThietBi = loaiThietBiRepository.findByTenLoai(tbBackup.getLoaiThietBi())
                                .orElse(null);
                            if (loaiThietBi != null) {
                                thietBi.setLoaiThietBi(loaiThietBi);
                                logger.info("Device type found: {}", loaiThietBi.getTenLoai());
                            } else {
                                logger.warn("Device type not found: {}, device will be created without type", tbBackup.getLoaiThietBi());
                            }
                        }
                        
                        thietBi = thietBiRepository.save(thietBi);
                        logger.info("Device saved: {} in zone {} with new token", thietBi.getTenThietBi(), khuVuc.getTenKhuVuc());
                    } else {
                        logger.warn("Zone not found for device {}, skipping", tbBackup.getTenThietBi());
                    }
                }
            } else {
                logger.info("No devices to restore for project {}", duAn.getTenDuAn());
            }

            restoredCount++;
        }

        logger.info("Import completed. Total projects restored: {}", restoredCount);
        logger.info("=== IMPORT END: User={} ===", email);
    }
}
