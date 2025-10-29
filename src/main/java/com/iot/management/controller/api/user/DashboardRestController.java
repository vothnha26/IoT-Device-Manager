package com.iot.management.controller.api.user;

import com.iot.management.model.entity.*;
import com.iot.management.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private NhatKyDuLieuRepository nhatKyDuLieuRepository;

    @Autowired
    private LichSuCanhBaoRepository lichSuCanhBaoRepository;

    @Autowired
    private LichTrinhRepository lichTrinhRepository;

    @Autowired
    private LenhDieuKhienRepository lenhDieuKhienRepository;

    @Autowired
    private PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    @Autowired
    private DuAnRepository duAnRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private DangKyGoiRepository dangKyGoiRepository;

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    /**
     * Thống kê thiết bị
     */
    @GetMapping("/devices")
    public ResponseEntity<?> getDeviceStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<ThietBi> devices = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());

        long total = devices.size();
        long active = devices.stream()
                .filter(d -> d.getTrangThai() != null && "hoat_dong".equalsIgnoreCase(d.getTrangThai()))
                .count();
        long offline = total - active;

        // Thống kê theo loại thiết bị
        Map<String, Long> byType = devices.stream()
                .filter(d -> d.getLoaiThietBi() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getLoaiThietBi().getTenLoai(),
                        Collectors.counting()));

        // Thống kê theo khu vực
        Map<String, Long> byArea = devices.stream()
                .filter(d -> d.getKhuVuc() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getKhuVuc().getTenKhuVuc(),
                        Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("active", active);
        result.put("offline", offline);
        result.put("byType", byType);
        result.put("byArea", byArea);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê khu vực
     */
    @GetMapping("/areas")
    public ResponseEntity<?> getAreaStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<KhuVuc> areas = khuVucRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());

        List<Map<String, Object>> areaDevices = new ArrayList<>();
        for (KhuVuc area : areas) {
            List<ThietBi> devices = thietBiRepository.findByKhuVuc_MaKhuVuc(area.getMaKhuVuc());
            Map<String, Object> areaData = new HashMap<>();
            areaData.put("name", area.getTenKhuVuc());
            areaData.put("deviceCount", devices.size());
            areaDevices.add(areaData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", areas.size());
        result.put("areaDevices", areaDevices);

        return ResponseEntity.ok(result);
    }

    /**
     * Dữ liệu cảm biến (24h gần nhất)
     */
    @GetMapping("/sensors")
    public ResponseEntity<?> getSensorData(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        List<ThietBi> devices = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());

        // Lấy dữ liệu sensor trong 24h
        List<NhatKyDuLieu> sensorData = new ArrayList<>();
        for (ThietBi device : devices) {
            List<NhatKyDuLieu> data = nhatKyDuLieuRepository.findByThietBi_MaThietBiAndThoiGianBetween(
                    device.getMaThietBi(), last24h, LocalDateTime.now(), null);
            if (data != null) {
                sensorData.addAll(data);
            }
        }

        // Group by hour
        Map<Integer, List<Double>> dataByHour = new HashMap<>();
        for (NhatKyDuLieu data : sensorData) {
            int hour = data.getThoiGian().getHour();
            try {
                double value = Double.parseDouble(data.getGiaTri());
                dataByHour.computeIfAbsent(hour, k -> new ArrayList<>()).add(value);
            } catch (NumberFormatException e) {
                // Skip non-numeric values
            }
        }

        // Calculate averages
        List<Map<String, Object>> hourlyData = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            List<Double> values = dataByHour.getOrDefault(hour, new ArrayList<>());
            double avg = values.isEmpty() ? 0 : values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            Map<String, Object> hourData = new HashMap<>();
            hourData.put("hour", hour);
            hourData.put("value", Math.round(avg * 100.0) / 100.0);
            hourlyData.add(hourData);
        }

        // Calculate min/max/avg
        double min = sensorData.stream()
                .map(NhatKyDuLieu::getGiaTri)
                .filter(s -> s.matches("-?\\d+(\\.\\d+)?"))
                .mapToDouble(Double::parseDouble)
                .min().orElse(0);

        double max = sensorData.stream()
                .map(NhatKyDuLieu::getGiaTri)
                .filter(s -> s.matches("-?\\d+(\\.\\d+)?"))
                .mapToDouble(Double::parseDouble)
                .max().orElse(0);

        double avg = sensorData.stream()
                .map(NhatKyDuLieu::getGiaTri)
                .filter(s -> s.matches("-?\\d+(\\.\\d+)?"))
                .mapToDouble(Double::parseDouble)
                .average().orElse(0);

        Map<String, Object> result = new HashMap<>();
        result.put("hourlyData", hourlyData);
        result.put("min", Math.round(min * 100.0) / 100.0);
        result.put("max", Math.round(max * 100.0) / 100.0);
        result.put("avg", Math.round(avg * 100.0) / 100.0);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê cảnh báo
     */
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlertStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<ThietBi> devices = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
        List<Long> deviceIds = devices.stream().map(ThietBi::getMaThietBi).collect(Collectors.toList());

        // Cảnh báo 24h - đếm thủ công
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        List<LichSuCanhBao> allAlerts = deviceIds.isEmpty() ? new ArrayList<>()
                : lichSuCanhBaoRepository.findTop100ByThietBiMaThietBiInOrderByThoiGianDesc(deviceIds);

        long alerts24h = allAlerts.stream()
                .filter(a -> a.getThoiGian().isAfter(last24h))
                .count();

        // Cảnh báo 7 ngày
        LocalDateTime last7d = LocalDateTime.now().minusDays(7);
        long alerts7d = allAlerts.stream()
                .filter(a -> a.getThoiGian().isAfter(last7d))
                .count();

        // Thống kê theo mức độ - sử dụng noiDung thay vì mucDo
        Map<String, Long> byLevel = allAlerts.stream()
                .collect(Collectors.groupingBy(
                        a -> {
                            String noiDung = a.getNoiDung();
                            if (noiDung == null)
                                return "INFO";
                            if (noiDung.contains("nguy hiểm") || noiDung.contains("DANGER"))
                                return "DANGER";
                            if (noiDung.contains("cảnh báo") || noiDung.contains("WARNING"))
                                return "WARNING";
                            return "INFO";
                        },
                        Collectors.counting()));

        // Lấy 10 cảnh báo gần nhất
        List<Map<String, Object>> latestAlerts = allAlerts.stream()
                .limit(10)
                .map(alert -> {
                    Map<String, Object> alertData = new HashMap<>();
                    alertData.put("id", alert.getMaCanhBao());
                    alertData.put("device", alert.getThietBi() != null ? alert.getThietBi().getTenThietBi() : "N/A");
                    alertData.put("message", alert.getNoiDung());

                    // Xác định level từ nội dung
                    String noiDung = alert.getNoiDung();
                    String level = "INFO";
                    if (noiDung != null) {
                        if (noiDung.contains("nguy hiểm") || noiDung.contains("DANGER"))
                            level = "DANGER";
                        else if (noiDung.contains("cảnh báo") || noiDung.contains("WARNING"))
                            level = "WARNING";
                    }
                    alertData.put("level", level);
                    alertData.put("time", alert.getThoiGian().toString());
                    return alertData;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("alerts24h", alerts24h);
        result.put("alerts7d", alerts7d);
        result.put("byLevel", byLevel);
        result.put("latestAlerts", latestAlerts);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê lịch trình và điều khiển
     */
    @GetMapping("/schedules")
    public ResponseEntity<?> getScheduleStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<ThietBi> devices = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
        List<Long> deviceIds = devices.stream().map(ThietBi::getMaThietBi).collect(Collectors.toList());

        // Thống kê lịch trình theo tần suất
        List<LichTrinh> schedules = deviceIds.isEmpty() ? new ArrayList<>()
                : lichTrinhRepository.findByThietBiMaThietBiIn(deviceIds);

        // Sử dụng ngayTrongTuan thay vì tanSuat
        Map<String, Long> byFrequency = schedules.stream()
                .collect(Collectors.groupingBy(
                        s -> {
                            String ngayTrongTuan = s.getNgayTrongTuan();
                            if (ngayTrongTuan == null || ngayTrongTuan.equals("*"))
                                return "DAILY";
                            if (ngayTrongTuan.contains(",")) {
                                int count = ngayTrongTuan.split(",").length;
                                return count >= 5 ? "WEEKDAYS" : "CUSTOM";
                            }
                            return "WEEKLY";
                        },
                        Collectors.counting()));

        // Lệnh điều khiển gần đây
        LocalDateTime last7d = LocalDateTime.now().minusDays(7);
        List<LenhDieuKhien> allCommands = deviceIds.isEmpty() ? new ArrayList<>()
                : lenhDieuKhienRepository.findTop50ByThietBi_MaThietBiOrderByNgayTaoDesc(deviceIds.get(0));

        // Filter recent commands
        List<LenhDieuKhien> commands = allCommands.stream()
                .filter(cmd -> cmd.getNgayTao().isAfter(last7d))
                .limit(10)
                .collect(Collectors.toList());

        List<Map<String, Object>> recentCommands = commands.stream()
                .map(cmd -> {
                    Map<String, Object> cmdData = new HashMap<>();
                    cmdData.put("id", cmd.getMaLenh());
                    cmdData.put("device", cmd.getThietBi() != null ? cmd.getThietBi().getTenThietBi() : "N/A");
                    cmdData.put("command",
                            cmd.getTenLenh() + (cmd.getGiaTriLenh() != null ? ": " + cmd.getGiaTriLenh() : ""));
                    cmdData.put("user", cmd.getNguoiGui() != null ? cmd.getNguoiGui().getTenDangNhap() : "System");
                    cmdData.put("time", cmd.getNgayTao().toString());
                    return cmdData;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", schedules.size());
        result.put("byFrequency", byFrequency);
        result.put("recentCommands", recentCommands);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê người dùng và phân quyền
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUserStats(Authentication authentication) {
        // Lấy user hiện tại theo email (authentication name)
        NguoiDung user = nguoiDungRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Lấy gói cước hiện tại
        Optional<DangKyGoi> activePackage = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(
                user.getMaNguoiDung(), DangKyGoi.TRANG_THAI_ACTIVE);

        int userLimit = 1; // Mặc định
        if (activePackage.isPresent()) {
            GoiCuoc goiCuoc = activePackage.get().getGoiCuoc();
            userLimit = goiCuoc.getSlNguoiDungToiDa() != null ? goiCuoc.getSlNguoiDungToiDa() : 1;
        }

        // Lấy tất cả dự án mà user là chủ sở hữu
        List<DuAn> ownedProjects = duAnRepository.findByNguoiDung(user);

        // Lấy danh sách thành viên từ phân quyền dự án
        List<Map<String, Object>> userHistory = new ArrayList<>();

        for (DuAn duAn : ownedProjects) {
            List<PhanQuyenDuAn> permissions = phanQuyenDuAnRepository.findByDuAn(duAn);

            for (PhanQuyenDuAn pq : permissions) {
                NguoiDung member = pq.getNguoiDung();

                // Bỏ qua chính user hiện tại (chủ sở hữu)
                if (member.getMaNguoiDung().equals(user.getMaNguoiDung())) {
                    continue;
                }

                // Thêm thành viên vào danh sách
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", member.getMaNguoiDung());
                userData.put("username", member.getTenDangNhap());
                userData.put("email", member.getEmail());
                userData.put("role", pq.getVaiTro().getDescription());
                userData.put("addedDate", pq.getNgayCapQuyen().toString());
                userData.put("projectName", duAn.getTenDuAn());
                userHistory.add(userData);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("currentUsers", userHistory.size());
        result.put("userLimit", userLimit);
        result.put("sharedUsers", userHistory.size());
        result.put("userHistory", userHistory);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê token và gói cước
     */
    @GetMapping("/tokens")
    public ResponseEntity<?> getTokenStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Lấy gói cước hiện tại
        Optional<DangKyGoi> activePackage = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(
                user.getMaNguoiDung(), DangKyGoi.TRANG_THAI_ACTIVE);

        int tokenLimit = 5; // Mặc định
        String packageName = "Free";
        long daysLeft = 0;

        if (activePackage.isPresent()) {
            DangKyGoi dangKy = activePackage.get();
            GoiCuoc goiCuoc = dangKy.getGoiCuoc();
            tokenLimit = goiCuoc.getSlTokenToiDa();
            packageName = goiCuoc.getTenGoi();

            if (dangKy.getNgayKetThuc() != null) {
                daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), dangKy.getNgayKetThuc());
                if (daysLeft < 0)
                    daysLeft = 0;
            }
        }

        // Đếm token đã sử dụng (thiết bị)
        List<ThietBi> devices = thietBiRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
        int tokenUsed = devices.size();

        // Đếm khu vực đã sử dụng
        List<KhuVuc> areas = khuVucRepository.findByChuSoHuu_MaNguoiDung(user.getMaNguoiDung());
        int areasUsed = areas.size();

        // Lấy giới hạn từ gói cước
        int areaLimit = 5; // Mặc định
        int deviceLimit = 10; // Mặc định

        if (activePackage.isPresent()) {
            GoiCuoc goiCuoc = activePackage.get().getGoiCuoc();
            areaLimit = goiCuoc.getSlKhuVucToiDa();
            deviceLimit = goiCuoc.getSlThietBiToiDa() != null ? goiCuoc.getSlThietBiToiDa() : 10;
        }

        // Dữ liệu sử dụng theo tháng (dựa trên ngày lắp đặt thiết bị)
        List<Map<String, Object>> monthlyUsage = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime startOfMonth = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

            // Đếm số thiết bị được lắp đặt đến cuối tháng đó
            long devicesCountByMonth = devices.stream()
                    .filter(d -> d.getNgayLapDat() != null &&
                            !d.getNgayLapDat().atStartOfDay().isAfter(endOfMonth))
                    .count();

            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", startOfMonth.getMonthValue() + "/" + startOfMonth.getYear());
            monthData.put("used", (int) devicesCountByMonth);
            monthlyUsage.add(monthData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tokenUsed", tokenUsed);
        result.put("tokenLimit", tokenLimit);
        result.put("areasUsed", areasUsed);
        result.put("areaLimit", areaLimit);
        result.put("devicesUsed", tokenUsed);
        result.put("deviceLimit", deviceLimit);
        result.put("packageName", packageName);
        result.put("daysLeft", daysLeft);
        result.put("monthlyUsage", monthlyUsage);

        return ResponseEntity.ok(result);
    }

    /**
     * Thống kê tài chính
     */
    @GetMapping("/finance")
    public ResponseEntity<?> getFinanceStats(Authentication authentication) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Lấy gói cước hiện tại
        Optional<DangKyGoi> activePackage = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(
                user.getMaNguoiDung(), DangKyGoi.TRANG_THAI_ACTIVE);

        String currentPackage = "Free";
        if (activePackage.isPresent()) {
            currentPackage = activePackage.get().getGoiCuoc().getTenGoi();
        }

        // Lấy lịch sử thanh toán - thông qua DangKyGoi
        List<ThanhToan> allPayments = thanhToanRepository.findAll();
        List<ThanhToan> payments = allPayments.stream()
                .filter(p -> p.getDangKyGoi() != null
                        && p.getDangKyGoi().getNguoiDung() != null
                        && p.getDangKyGoi().getNguoiDung().getMaNguoiDung().equals(user.getMaNguoiDung()))
                .sorted((p1, p2) -> p2.getNgayThanhToan().compareTo(p1.getNgayThanhToan()))
                .collect(Collectors.toList());

        // Tính tổng theo tháng
        LocalDateTime now = LocalDateTime.now();
        double monthTotal = payments.stream()
                .filter(p -> p.getNgayThanhToan().getMonthValue() == now.getMonthValue()
                        && p.getNgayThanhToan().getYear() == now.getYear()
                        && "SUCCESS".equalsIgnoreCase(p.getTrangThai()))
                .mapToDouble(p -> p.getSoTien().doubleValue())
                .sum();

        // Tính tổng theo quý
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
        double quarterTotal = payments.stream()
                .filter(p -> {
                    int paymentQuarter = (p.getNgayThanhToan().getMonthValue() - 1) / 3 + 1;
                    return paymentQuarter == currentQuarter
                            && p.getNgayThanhToan().getYear() == now.getYear()
                            && "SUCCESS".equalsIgnoreCase(p.getTrangThai());
                })
                .mapToDouble(p -> p.getSoTien().doubleValue())
                .sum();

        // Lấy 10 thanh toán gần nhất
        List<Map<String, Object>> recentPayments = payments.stream()
                .limit(10)
                .map(payment -> {
                    Map<String, Object> paymentData = new HashMap<>();
                    paymentData.put("id", payment.getMaThanhToan());
                    paymentData.put("amount", payment.getSoTien());
                    paymentData.put("date", payment.getNgayThanhToan().toString());
                    paymentData.put("status", payment.getTrangThai());
                    paymentData.put("package",
                            payment.getDangKyGoi() != null && payment.getDangKyGoi().getGoiCuoc() != null
                                    ? payment.getDangKyGoi().getGoiCuoc().getTenGoi()
                                    : "N/A");
                    return paymentData;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("monthTotal", Math.round(monthTotal * 100.0) / 100.0);
        result.put("quarterTotal", Math.round(quarterTotal * 100.0) / 100.0);
        result.put("currentPackage", currentPackage);
        result.put("recentPayments", recentPayments);

        return ResponseEntity.ok(result);
    }
}
