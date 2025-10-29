package com.iot.management.controller.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iot.management.model.entity.DangKyGoi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.repository.DangKyGoiRepository;
import com.iot.management.repository.DuAnRepository;
import com.iot.management.repository.KhuVucRepository;
import com.iot.management.repository.LoaiThietBiRepository;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.ThanhToanRepository;
import com.iot.management.repository.ThietBiRepository;
import com.iot.management.repository.ThongBaoRepository;
import com.iot.management.security.SecurityUser;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private LoaiThietBiRepository loaiThietBiRepository;

    @Autowired
    private DuAnRepository duAnRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Autowired
    private DangKyGoiRepository dangKyGoiRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // Set basic info
        model.addAttribute("title", "Admin Dashboard - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        // Get statistics
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalDevices", thietBiRepository.count());
        model.addAttribute("totalDeviceTypes", loaiThietBiRepository.count());
        model.addAttribute("totalAreas", khuVucRepository.count());
        model.addAttribute("totalProjects", duAnRepository.count());

        // Get today's notifications count
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long todayNotifications = thongBaoRepository.countByThoiGianTaoBetween(startOfDay, endOfDay);
        model.addAttribute("todayNotifications", todayNotifications);

        return "admin/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Admin Profile - IoT Manager");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        return "admin/profile";
    }

    @GetMapping("/notifications")
    public String notifications(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Thông báo - Admin");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        return "admin/notifications";
    }

    @GetMapping("/statistics")
    public String statistics(Model model, @AuthenticationPrincipal SecurityUser currentUser) {
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Thống kê hệ thống - Admin");
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("user", currentUser);

        return "admin/statistics";
    }

    /**
     * API: Lấy danh sách thông báo admin (đăng ký mới, hết hạn)
     */
    @GetMapping("/api/notifications")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdminNotifications() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> notifications = new ArrayList<>();

            // Lấy các đăng ký mới trong 7 ngày gần đây
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            List<DangKyGoi> recentRegistrations = dangKyGoiRepository.findAll().stream()
                    .filter(dk -> dk.getNgayBatDau() != null && dk.getNgayBatDau().isAfter(sevenDaysAgo))
                    .sorted((a, b) -> b.getNgayBatDau().compareTo(a.getNgayBatDau()))
                    .limit(50)
                    .toList();

            for (DangKyGoi dk : recentRegistrations) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("type", "registration");
                notif.put("title", "Đăng ký gói mới");
                notif.put("message", (dk.getNguoiDung() != null ? dk.getNguoiDung().getTenDangNhap() : "Người dùng")
                        + " đã đăng ký gói " + (dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : ""));
                notif.put("time", dk.getNgayBatDau());
                notif.put("userId", dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null);
                notif.put("packageName", dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : "");
                notifications.add(notif);
            }

            // Lấy các gói sắp hết hạn (trong 7 ngày tới)
            LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
            List<DangKyGoi> expiringPackages = dangKyGoiRepository.findAll().stream()
                    .filter(dk -> DangKyGoi.TRANG_THAI_ACTIVE.equals(dk.getTrangThai())
                            && dk.getNgayKetThuc() != null
                            && dk.getNgayKetThuc().isBefore(sevenDaysLater)
                            && dk.getNgayKetThuc().isAfter(LocalDateTime.now()))
                    .sorted((a, b) -> a.getNgayKetThuc().compareTo(b.getNgayKetThuc()))
                    .limit(50)
                    .toList();

            for (DangKyGoi dk : expiringPackages) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("type", "expiring");
                notif.put("title", "Gói sắp hết hạn");
                notif.put("message", "Gói " + (dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : "")
                        + " của " + (dk.getNguoiDung() != null ? dk.getNguoiDung().getTenDangNhap() : "người dùng")
                        + " sẽ hết hạn");
                notif.put("time", dk.getNgayKetThuc());
                notif.put("userId", dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null);
                notif.put("packageName", dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : "");
                notifications.add(notif);
            }

            // Lấy các gói đã hết hạn gần đây (7 ngày)
            List<DangKyGoi> expiredPackages = dangKyGoiRepository.findAll().stream()
                    .filter(dk -> DangKyGoi.TRANG_THAI_EXPIRED.equals(dk.getTrangThai())
                            && dk.getNgayKetThuc() != null
                            && dk.getNgayKetThuc().isAfter(sevenDaysAgo))
                    .sorted((a, b) -> b.getNgayKetThuc().compareTo(a.getNgayKetThuc()))
                    .limit(50)
                    .toList();

            for (DangKyGoi dk : expiredPackages) {
                Map<String, Object> notif = new HashMap<>();
                notif.put("type", "expired");
                notif.put("title", "Gói đã hết hạn");
                notif.put("message", "Gói " + (dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : "")
                        + " của " + (dk.getNguoiDung() != null ? dk.getNguoiDung().getTenDangNhap() : "người dùng")
                        + " đã hết hạn");
                notif.put("time", dk.getNgayKetThuc());
                notif.put("userId", dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null);
                notif.put("packageName", dk.getGoiCuoc() != null ? dk.getGoiCuoc().getTenGoi() : "");
                notifications.add(notif);
            }

            // Sắp xếp theo thời gian giảm dần
            notifications.sort((a, b) -> {
                LocalDateTime timeA = (LocalDateTime) a.get("time");
                LocalDateTime timeB = (LocalDateTime) b.get("time");
                return timeB.compareTo(timeA);
            });

            response.put("success", true);
            response.put("notifications", notifications);
            response.put("total", notifications.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Lấy thông tin chi tiết người dùng
     */
    @GetMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            NguoiDung nguoiDung = nguoiDungRepository.findById(userId).orElse(null);

            if (nguoiDung == null) {
                response.put("success", false);
                response.put("error", "Không tìm thấy người dùng");
                return ResponseEntity.status(404).body(response);
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("maNguoiDung", nguoiDung.getMaNguoiDung());
            userInfo.put("tenDangNhap", nguoiDung.getTenDangNhap());
            userInfo.put("email", nguoiDung.getEmail());

            // Lấy vai trò đầu tiên
            String vaiTro = "USER";
            if (nguoiDung.getVaiTro() != null && !nguoiDung.getVaiTro().isEmpty()) {
                vaiTro = nguoiDung.getVaiTro().iterator().next().getTenVaiTro();
            }
            userInfo.put("vaiTro", vaiTro);
            userInfo.put("daKichHoat", nguoiDung.getKichHoat() != null ? nguoiDung.getKichHoat() : false);
            userInfo.put("ngayTao", nguoiDung.getNgayTao());

            // Lấy gói cước hiện tại
            DangKyGoi activePackage = dangKyGoiRepository
                    .findByNguoiDung_MaNguoiDungAndTrangThai(userId, "ACTIVE")
                    .orElse(null);

            if (activePackage != null && activePackage.getGoiCuoc() != null) {
                userInfo.put("currentPackage", activePackage.getGoiCuoc().getTenGoi());
            } else {
                userInfo.put("currentPackage", "Chưa có gói");
            }

            // Đếm số lần đăng ký
            long registrationCount = dangKyGoiRepository.findAll().stream()
                    .filter(dk -> dk.getNguoiDung() != null &&
                            dk.getNguoiDung().getMaNguoiDung().equals(userId))
                    .count();
            userInfo.put("registrationCount", registrationCount);

            // Đếm số thiết bị
            long deviceCount = thietBiRepository.findByChuSoHuu_MaNguoiDung(userId).size();
            userInfo.put("deviceCount", deviceCount);

            // Đếm số khu vực
            long areaCount = khuVucRepository.countByChuSoHuu_MaNguoiDung(userId);
            userInfo.put("areaCount", areaCount);

            response.put("success", true);
            response.put("user", userInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Thống kê tỉ lệ gói đang hoạt động / hết hạn
     */
    @GetMapping("/api/package-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPackageStats() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<DangKyGoi> allPackages = dangKyGoiRepository.findAll();

            long activeCount = allPackages.stream()
                    .filter(dk -> DangKyGoi.TRANG_THAI_ACTIVE.equals(dk.getTrangThai()))
                    .count();

            long expiredCount = allPackages.stream()
                    .filter(dk -> DangKyGoi.TRANG_THAI_EXPIRED.equals(dk.getTrangThai()))
                    .count();

            long cancelledCount = allPackages.stream()
                    .filter(dk -> DangKyGoi.TRANG_THAI_CANCELLED.equals(dk.getTrangThai()))
                    .count();

            long total = allPackages.size();

            response.put("active", activeCount);
            response.put("expired", expiredCount);
            response.put("cancelled", cancelledCount);
            response.put("total", total);

            // Tính phần trăm
            if (total > 0) {
                response.put("activePercent", Math.round((activeCount * 100.0) / total));
                response.put("expiredPercent", Math.round((expiredCount * 100.0) / total));
                response.put("cancelledPercent", Math.round((cancelledCount * 100.0) / total));
            } else {
                response.put("activePercent", 0);
                response.put("expiredPercent", 0);
                response.put("cancelledPercent", 0);
            }

            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Doanh thu theo tháng (12 tháng gần nhất)
     */
    @GetMapping("/api/revenue-by-month")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRevenueByMonth() {
        Map<String, Object> response = new HashMap<>();

        try {
            YearMonth currentMonth = YearMonth.now();
            List<String> months = new ArrayList<>();
            List<BigDecimal> revenues = new ArrayList<>();

            // Lấy dữ liệu 12 tháng gần nhất
            for (int i = 11; i >= 0; i--) {
                YearMonth month = currentMonth.minusMonths(i);
                LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
                LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

                // Lấy tổng doanh thu từ các giao dịch thành công
                BigDecimal monthRevenue = thanhToanRepository
                        .findByNgayThanhToanBetweenAndTrangThai(startOfMonth, endOfMonth, "DA_THANH_TOAN")
                        .stream()
                        .map(tt -> tt.getSoTien() != null ? tt.getSoTien() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                months.add(month.getMonth().toString().substring(0, 3) + " " + month.getYear());
                revenues.add(monthRevenue);
            }

            response.put("months", months);
            response.put("revenues", revenues);
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Thống kê tổng quan hệ thống
     */
    @GetMapping("/api/system-overview")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("totalUsers", nguoiDungRepository.count());
            response.put("totalProjects", duAnRepository.count());
            response.put("totalDevices", thietBiRepository.count());
            response.put("totalAreas", khuVucRepository.count());
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Thống kê người dùng theo gói
     */
    @GetMapping("/api/users-by-package")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUsersByPackage() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<DangKyGoi> activePackages = dangKyGoiRepository
                    .findByTrangThai(DangKyGoi.TRANG_THAI_ACTIVE);

            // Đếm người dùng theo gói
            Map<String, Long> packageCounts = new HashMap<>();
            for (DangKyGoi dk : activePackages) {
                if (dk.getGoiCuoc() != null) {
                    String packageName = dk.getGoiCuoc().getTenGoi();
                    packageCounts.put(packageName, packageCounts.getOrDefault(packageName, 0L) + 1);
                }
            }

            // Đếm người dùng chưa có gói
            long totalUsers = nguoiDungRepository.count();
            long usersWithPackage = activePackages.stream()
                    .map(dk -> dk.getNguoiDung() != null ? dk.getNguoiDung().getMaNguoiDung() : null)
                    .filter(id -> id != null)
                    .distinct()
                    .count();
            long usersWithoutPackage = totalUsers - usersWithPackage;

            if (usersWithoutPackage > 0) {
                packageCounts.put("Chưa có gói", usersWithoutPackage);
            }

            response.put("packages", new ArrayList<>(packageCounts.keySet()));
            response.put("counts", new ArrayList<>(packageCounts.values()));
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API: Lấy danh sách người dùng đang sử dụng một gói cụ thể
     */
    @GetMapping("/api/packages/{packageId}/users")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPackageUsers(@PathVariable Long packageId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<DangKyGoi> registrations = dangKyGoiRepository.findAll().stream()
                    .filter(dk -> dk.getGoiCuoc() != null &&
                            dk.getGoiCuoc().getMaGoiCuoc().equals(packageId) &&
                            DangKyGoi.TRANG_THAI_ACTIVE.equals(dk.getTrangThai()))
                    .toList();

            List<Map<String, Object>> users = new ArrayList<>();
            for (DangKyGoi dk : registrations) {
                if (dk.getNguoiDung() != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    NguoiDung user = dk.getNguoiDung();

                    userInfo.put("maNguoiDung", user.getMaNguoiDung());
                    userInfo.put("tenDangNhap", user.getTenDangNhap());
                    userInfo.put("email", user.getEmail());
                    userInfo.put("ngayBatDau", dk.getNgayBatDau());
                    userInfo.put("ngayKetThuc", dk.getNgayKetThuc());
                    userInfo.put("trangThai", dk.getTrangThai());

                    // Đếm số thiết bị của người dùng
                    long deviceCount = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung()).size();
                    userInfo.put("soThietBi", deviceCount);

                    // Đếm số khu vực của người dùng
                    long areaCount = khuVucRepository.countByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
                    userInfo.put("soKhuVuc", areaCount);

                    users.add(userInfo);
                }
            }

            response.put("success", true);
            response.put("users", users);
            response.put("total", users.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}