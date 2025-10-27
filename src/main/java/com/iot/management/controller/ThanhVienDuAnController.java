package com.iot.management.controller;

import com.iot.management.model.entity.*;
import com.iot.management.model.enums.DuAnRole;
import com.iot.management.model.repository.*;
import com.iot.management.repository.LoiMoiDuAnRepository;
import com.iot.management.service.DuAnAuthorizationService;
import com.iot.management.service.EmailService;
import com.iot.management.service.PhanQuyenService;
import com.iot.management.dto.AreaPermissionDTO;
import com.iot.management.dto.DevicePermissionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/du-an")
public class ThanhVienDuAnController {

    @Autowired
    private DuAnRepository duAnRepository;

    @Autowired
    private PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private DuAnAuthorizationService duAnAuthService;

    @Autowired
    private LoiMoiDuAnRepository loiMoiDuAnRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ThongBaoRepository thongBaoRepository;
    
    @Autowired
    private KhuVucRepository khuVucRepository;
    
    @Autowired
    private ThietBiRepository thietBiRepository;
    
    @Autowired
    private PhanQuyenKhuVucRepository phanQuyenKhuVucRepository;
    
    @Autowired
    private PhanQuyenThietBiRepository phanQuyenThietBiRepository;
    
    @Autowired
    private PhanQuyenService phanQuyenService;

    /**
     * Trang quản lý thành viên của dự án
     */
    @GetMapping("/{id}/thanh-vien")
    public String quanLyThanhVien(@PathVariable("id") Long maDuAn,
                                   Authentication auth,
                                   Model model) {
        NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        DuAn duAn = duAnRepository.findById(maDuAn)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));

        // Kiểm tra quyền xem thành viên
        if (!duAnAuthService.coQuyenXemDuAn(maDuAn, currentUser.getMaNguoiDung())) {
            throw new RuntimeException("Bạn không có quyền xem dự án này");
        }

        // Kiểm tra quyền cấp quyền (CHU_SO_HUU hoặc QUAN_LY)
        DuAnRole vaiTro = duAnAuthService.layVaiTroTrongDuAn(maDuAn, currentUser.getMaNguoiDung());
        boolean coQuyenCapQuyen = (vaiTro == DuAnRole.CHU_SO_HUU || vaiTro == DuAnRole.QUAN_LY);
        boolean laChuSoHuu = (vaiTro == DuAnRole.CHU_SO_HUU);

        // Lấy danh sách thành viên
        List<PhanQuyenDuAn> members = phanQuyenDuAnRepository.findByDuAn(duAn);

        // Thống kê
        Map<String, Long> stats = new HashMap<>();
        stats.put("tongSo", (long) members.size());
        stats.put("chuSoHuu", members.stream()
                .filter(m -> m.getVaiTro() == DuAnRole.CHU_SO_HUU).count());
        stats.put("quanLy", members.stream()
                .filter(m -> m.getVaiTro() == DuAnRole.QUAN_LY).count());
        stats.put("nguoiDung", members.stream()
                .filter(m -> m.getVaiTro() == DuAnRole.NGUOI_DUNG).count());

        model.addAttribute("duAn", duAn);
        model.addAttribute("members", members);
        model.addAttribute("stats", stats);
        model.addAttribute("coQuyenCapQuyen", coQuyenCapQuyen);
        model.addAttribute("laChuSoHuu", laChuSoHuu);

        return "du-an/thanh-vien";
    }

    /**
     * API: Mời thành viên vào dự án
     */
    @PostMapping("/{id}/thanh-vien/moi")
    @ResponseBody
    public ResponseEntity<?> moiThanhVien(@PathVariable("id") Long maDuAn,
                                          @RequestBody MoiThanhVienRequest request,
                                          Authentication auth) {
        try {
            NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            DuAn duAn = duAnRepository.findById(maDuAn)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));

            // Kiểm tra quyền cấp quyền
            DuAnRole vaiTroHienTai = duAnAuthService.layVaiTroTrongDuAn(maDuAn, currentUser.getMaNguoiDung());
            if (vaiTroHienTai != DuAnRole.CHU_SO_HUU && vaiTroHienTai != DuAnRole.QUAN_LY) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền mời thành viên vào dự án này"
                ));
            }

            // Kiểm tra email tồn tại
            NguoiDung nguoiDuocMoi = nguoiDungRepository.findByEmail(request.getEmail())
                    .orElse(null);

            if (nguoiDuocMoi == null) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Không tìm thấy người dùng với email: " + request.getEmail()
                ));
            }

            // Kiểm tra đã là thành viên chưa
            boolean daTonTai = phanQuyenDuAnRepository
                    .findByDuAnAndNguoiDung(duAn, nguoiDuocMoi)
                    .isPresent();

            if (daTonTai) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Người dùng này đã là thành viên của dự án"
                ));
            }

            // Kiểm tra có lời mời pending không
            Optional<LoiMoiDuAn> loiMoiCu = loiMoiDuAnRepository
                    .findByEmailNguoiNhanAndDuAnMaDuAnAndTrangThai(request.getEmail(), maDuAn, "PENDING");
            
            if (loiMoiCu.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Đã có lời mời chưa được chấp nhận cho người dùng này"
                ));
            }

            // Kiểm tra vai trò hợp lệ
            DuAnRole vaiTro;
            try {
                vaiTro = DuAnRole.valueOf(request.getVaiTro());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Vai trò không hợp lệ"
                ));
            }

            // Chỉ CHU_SO_HUU mới có thể mời QUAN_LY
            if (vaiTro == DuAnRole.QUAN_LY && vaiTroHienTai != DuAnRole.CHU_SO_HUU) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Chỉ chủ sở hữu mới có thể mời người quản lý"
                ));
            }

            // Tạo token ngẫu nhiên
            String token = UUID.randomUUID().toString();

            // Tạo lời mời
            LoiMoiDuAn loiMoi = new LoiMoiDuAn();
            loiMoi.setDuAn(duAn);
            loiMoi.setEmailNguoiNhan(request.getEmail());
            loiMoi.setNguoiMoi(currentUser);
            loiMoi.setVaiTro(vaiTro);
            loiMoi.setToken(token);
            loiMoi.setNgayTao(LocalDateTime.now());
            loiMoi.setNgayHetHan(LocalDateTime.now().plusDays(7)); // Hết hạn sau 7 ngày
            loiMoi.setTrangThai("PENDING");

            loiMoiDuAnRepository.save(loiMoi);

            // Gửi email
            String acceptLink = "http://localhost:8080/du-an/loi-moi/chap-nhan?token=" + token;
            String rejectLink = "http://localhost:8080/du-an/loi-moi/tu-choi?token=" + token;
            
            String emailContent = String.format(
                    "Xin chào,\n\n" +
                    "%s đã mời bạn tham gia dự án \"%s\" với vai trò %s.\n\n" +
                    "Chấp nhận lời mời: %s\n\n" +
                    "Từ chối lời mời: %s\n\n" +
                    "Lời mời này sẽ hết hạn sau 7 ngày.\n\n" +
                    "Trân trọng,\n" +
                    "IoT Management Team",
                    currentUser.getTenDangNhap(),
                    duAn.getTenDuAn(),
                    vaiTro.getDescription(),
                    acceptLink,
                    rejectLink
            );

            emailService.sendSimpleEmail(
                    request.getEmail(),
                    "Lời mời tham gia dự án " + duAn.getTenDuAn(),
                    emailContent
            );

            // Tạo thông báo cho người được mời (nếu đã có tài khoản)
            if (nguoiDuocMoi != null) {
                ThongBao thongBao = new ThongBao(
                        nguoiDuocMoi,
                        "Lời mời tham gia dự án",
                        currentUser.getTenDangNhap() + " đã mời bạn tham gia dự án \"" + 
                        duAn.getTenDuAn() + "\" với vai trò " + vaiTro.getDescription() + 
                        ". Vui lòng kiểm tra email để chấp nhận lời mời.",
                        "INFO"
                );
                thongBao.setUrlLienKet("/du-an/" + maDuAn + "/thanh-vien");
                thongBaoRepository.save(thongBao);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã gửi lời mời qua email thành công"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    /**
     * API: Xóa thành viên khỏi dự án
     */
    @DeleteMapping("/thanh-vien/xoa/{id}")
    @ResponseBody
    public ResponseEntity<?> xoaThanhVien(@PathVariable("id") Long maPhanQuyen,
                                          Authentication auth) {
        try {
            NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            PhanQuyenDuAn phanQuyen = phanQuyenDuAnRepository.findById(maPhanQuyen)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phân quyền"));

            Long maDuAn = phanQuyen.getDuAn().getMaDuAn();

            // Kiểm tra quyền xóa thành viên
            DuAnRole vaiTroHienTai = duAnAuthService.layVaiTroTrongDuAn(maDuAn, currentUser.getMaNguoiDung());
            if (vaiTroHienTai != DuAnRole.CHU_SO_HUU && vaiTroHienTai != DuAnRole.QUAN_LY) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền xóa thành viên khỏi dự án này"
                ));
            }

            // Không thể xóa chủ sở hữu
            if (phanQuyen.getVaiTro() == DuAnRole.CHU_SO_HUU) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Không thể xóa chủ sở hữu khỏi dự án"
                ));
            }

            phanQuyenDuAnRepository.delete(phanQuyen);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã xóa thành viên thành công"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    /**
     * API: Cập nhật vai trò thành viên
     */
    @PutMapping("/{duAnId}/thanh-vien/{id}/vai-tro")
    @ResponseBody
    public ResponseEntity<?> capNhatVaiTro(@PathVariable("duAnId") Long duAnId,
                                           @PathVariable("id") Long maPhanQuyen,
                                           @RequestBody CapNhatVaiTroRequest request,
                                           Authentication auth) {
        try {
            NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            PhanQuyenDuAn phanQuyen = phanQuyenDuAnRepository.findById(maPhanQuyen)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phân quyền"));

            // Kiểm tra quyền cập nhật vai trò
            DuAnRole vaiTroHienTai = duAnAuthService.layVaiTroTrongDuAn(duAnId, currentUser.getMaNguoiDung());
            if (vaiTroHienTai != DuAnRole.CHU_SO_HUU && vaiTroHienTai != DuAnRole.QUAN_LY) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Bạn không có quyền cập nhật vai trò thành viên"
                ));
            }

            // Không thể thay đổi vai trò chủ sở hữu
            if (phanQuyen.getVaiTro() == DuAnRole.CHU_SO_HUU) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Không thể thay đổi vai trò của chủ sở hữu"
                ));
            }

            // Kiểm tra vai trò mới hợp lệ
            DuAnRole vaiTroMoi;
            try {
                vaiTroMoi = DuAnRole.valueOf(request.getVaiTro());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Vai trò không hợp lệ"
                ));
            }

            // Không thể chuyển thành CHU_SO_HUU
            if (vaiTroMoi == DuAnRole.CHU_SO_HUU) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Không thể chuyển vai trò thành chủ sở hữu"
                ));
            }

            // Chỉ CHU_SO_HUU mới có thể bổ nhiệm QUAN_LY
            if (vaiTroMoi == DuAnRole.QUAN_LY && vaiTroHienTai != DuAnRole.CHU_SO_HUU) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Chỉ chủ sở hữu mới có thể bổ nhiệm người quản lý"
                ));
            }

            phanQuyen.setVaiTro(vaiTroMoi);
            phanQuyenDuAnRepository.save(phanQuyen);
            
            // Nếu nâng lên QUAN_LY, xóa các phân quyền chi tiết (không cần nữa vì có full access)
            if (vaiTroMoi == DuAnRole.QUAN_LY) {
                Long userId = phanQuyen.getNguoiDung().getMaNguoiDung();
                List<PhanQuyenKhuVuc> areaPerms = phanQuyenKhuVucRepository.findByMaNguoiDungAndMaDuAn(userId, duAnId);
                if (!areaPerms.isEmpty()) {
                    phanQuyenKhuVucRepository.deleteAll(areaPerms);
                }
                
                List<PhanQuyenThietBi> devicePerms = phanQuyenThietBiRepository.findByMaNguoiDungAndMaDuAn(userId, duAnId);
                if (!devicePerms.isEmpty()) {
                    phanQuyenThietBiRepository.deleteAll(devicePerms);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Đã cập nhật vai trò thành công"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Có lỗi xảy ra: " + e.getMessage()
            ));
        }
    }

    /**
     * Chấp nhận lời mời tham gia dự án
     */
    @GetMapping("/loi-moi/chap-nhan")
    public String chapNhanLoiMoi(@RequestParam("token") String token,
                                  Authentication auth,
                                  Model model,
                                  HttpServletRequest request) {
        try {
            // Nếu chưa đăng nhập, chuyển đến trang đăng nhập với redirect URL
            if (auth == null) {
                return "redirect:/auth/login?redirect=" + 
                       java.net.URLEncoder.encode("/du-an/loi-moi/chap-nhan?token=" + token, "UTF-8");
            }

            NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            LoiMoiDuAn loiMoi = loiMoiDuAnRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Lời mời không tồn tại"));

            // Kiểm tra trạng thái
            if (!"PENDING".equals(loiMoi.getTrangThai())) {
                model.addAttribute("message", "Lời mời này đã được xử lý trước đó");
                model.addAttribute("success", false);
                return "du-an/loi-moi-result";
            }

            // Kiểm tra hết hạn
            if (LocalDateTime.now().isAfter(loiMoi.getNgayHetHan())) {
                loiMoi.setTrangThai("EXPIRED");
                loiMoiDuAnRepository.save(loiMoi);
                model.addAttribute("message", "Lời mời đã hết hạn");
                model.addAttribute("success", false);
                return "du-an/loi-moi-result";
            }

            // Kiểm tra email khớp
            if (!currentUser.getEmail().equals(loiMoi.getEmailNguoiNhan())) {
                model.addAttribute("message", "Bạn không có quyền chấp nhận lời mời này");
                model.addAttribute("success", false);
                return "du-an/loi-moi-result";
            }

            // Kiểm tra đã là thành viên chưa
            boolean daTonTai = phanQuyenDuAnRepository
                    .findByDuAnAndNguoiDung(loiMoi.getDuAn(), currentUser)
                    .isPresent();

            if (daTonTai) {
                loiMoi.setTrangThai("ACCEPTED");
                loiMoiDuAnRepository.save(loiMoi);
                model.addAttribute("message", "Bạn đã là thành viên của dự án này rồi");
                model.addAttribute("success", false);
                return "du-an/loi-moi-result";
            }

            // Tạo phân quyền mới
            PhanQuyenDuAn phanQuyen = new PhanQuyenDuAn();
            phanQuyen.setDuAn(loiMoi.getDuAn());
            phanQuyen.setNguoiDung(currentUser);
            phanQuyen.setVaiTro(loiMoi.getVaiTro());
            phanQuyen.setNgayCapQuyen(LocalDateTime.now());

            phanQuyenDuAnRepository.save(phanQuyen);

            // Cập nhật trạng thái lời mời
            loiMoi.setTrangThai("ACCEPTED");
            loiMoiDuAnRepository.save(loiMoi);

            // Gửi thông báo cho người mời
            ThongBao thongBaoChoNguoiMoi = new ThongBao(
                    loiMoi.getNguoiMoi(),
                    "Lời mời đã được chấp nhận",
                    currentUser.getTenDangNhap() + " đã chấp nhận lời mời tham gia dự án \"" + 
                    loiMoi.getDuAn().getTenDuAn() + "\" với vai trò " + loiMoi.getVaiTro().getDescription() + ".",
                    "SUCCESS"
            );
            thongBaoChoNguoiMoi.setUrlLienKet("/du-an/" + loiMoi.getDuAn().getMaDuAn() + "/thanh-vien");
            thongBaoRepository.save(thongBaoChoNguoiMoi);

            model.addAttribute("message", "Đã chấp nhận lời mời thành công! Bạn đã trở thành thành viên của dự án " + loiMoi.getDuAn().getTenDuAn());
            model.addAttribute("success", true);
            model.addAttribute("duAnId", loiMoi.getDuAn().getMaDuAn());
            return "du-an/loi-moi-result";

        } catch (Exception e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("success", false);
            return "du-an/loi-moi-result";
        }
    }

    /**
     * Từ chối lời mời tham gia dự án
     */
    @GetMapping("/loi-moi/tu-choi")
    public String tuChoiLoiMoi(@RequestParam("token") String token,
                                Authentication auth,
                                Model model,
                                HttpServletRequest request) {
        try {
            // Nếu chưa đăng nhập, chuyển đến trang đăng nhập với redirect URL
            if (auth == null) {
                return "redirect:/auth/login?redirect=" + 
                       java.net.URLEncoder.encode("/du-an/loi-moi/tu-choi?token=" + token, "UTF-8");
            }

            NguoiDung currentUser = nguoiDungRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

            LoiMoiDuAn loiMoi = loiMoiDuAnRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Lời mời không tồn tại"));

            // Kiểm tra email khớp
            if (!currentUser.getEmail().equals(loiMoi.getEmailNguoiNhan())) {
                model.addAttribute("message", "Bạn không có quyền từ chối lời mời này");
                model.addAttribute("success", false);
                return "du-an/loi-moi-result";
            }

            // Cập nhật trạng thái
            loiMoi.setTrangThai("REJECTED");
            loiMoiDuAnRepository.save(loiMoi);

            // Gửi thông báo cho người mời
            ThongBao thongBaoChoNguoiMoi = new ThongBao(
                    loiMoi.getNguoiMoi(),
                    "Lời mời đã bị từ chối",
                    currentUser.getTenDangNhap() + " đã từ chối lời mời tham gia dự án \"" + 
                    loiMoi.getDuAn().getTenDuAn() + "\".",
                    "WARNING"
            );
            thongBaoChoNguoiMoi.setUrlLienKet("/du-an/" + loiMoi.getDuAn().getMaDuAn() + "/thanh-vien");
            thongBaoRepository.save(thongBaoChoNguoiMoi);

            model.addAttribute("message", "Đã từ chối lời mời");
            model.addAttribute("success", true);
            return "du-an/loi-moi-result";

        } catch (Exception e) {
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("success", false);
            return "du-an/loi-moi-result";
        }
    }
    
    /**
     * Lấy phân quyền chi tiết của thành viên (khu vực và thiết bị)
     */
    @GetMapping("/{duAnId}/thanh-vien/{userId}/permissions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDetailPermissions(
            @PathVariable Long duAnId,
            @PathVariable Long userId,
            Authentication auth) {
        
        try {
            String username = auth.getName();
            NguoiDung currentUser = nguoiDungRepository.findByTenDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            DuAn duAn = duAnRepository.findById(duAnId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));
            
            // Kiểm tra quyền quản lý
            if (!duAnAuthService.laQuanLyTroLen(duAn.getMaDuAn(), currentUser.getMaNguoiDung())) {
                return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền quản lý dự án này"));
            }
            
            NguoiDung targetUser = nguoiDungRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            // Kiểm tra vai trò của user trong dự án
            DuAnRole userRole = duAnAuthService.layVaiTroTrongDuAn(duAnId, userId);
            boolean hasFullAccess = (userRole == DuAnRole.CHU_SO_HUU || userRole == DuAnRole.QUAN_LY);
            
            // Lấy tất cả khu vực trong dự án
            List<KhuVuc> allAreas = khuVucRepository.findByDuAnMaDuAn(duAnId);
            List<PhanQuyenKhuVuc> userAreaPerms = phanQuyenKhuVucRepository.findByMaNguoiDungAndMaDuAn(userId, duAnId);
            
            Map<Long, PhanQuyenKhuVuc> areaPermMap = new HashMap<>();
            for (PhanQuyenKhuVuc perm : userAreaPerms) {
                areaPermMap.put(perm.getKhuVuc().getMaKhuVuc(), perm);
            }
            
            List<Map<String, Object>> areaList = new ArrayList<>();
            for (KhuVuc area : allAreas) {
                Map<String, Object> areaData = new HashMap<>();
                areaData.put("maKhuVuc", area.getMaKhuVuc());
                areaData.put("tenKhuVuc", area.getTenKhuVuc());
                areaData.put("moTa", area.getMoTa());
                
                // Nếu user có quyền QUAN_LY hoặc CHU_SO_HUU ở cấp dự án, tự động có quyền tất cả khu vực
                if (hasFullAccess) {
                    areaData.put("hasPermission", true);
                    areaData.put("vaiTro", "QUAN_LY_KHU_VUC");
                    areaData.put("inheritedFromProject", true); // Đánh dấu là quyền được thừa kế từ dự án
                } else {
                    PhanQuyenKhuVuc perm = areaPermMap.get(area.getMaKhuVuc());
                    areaData.put("hasPermission", perm != null);
                    areaData.put("vaiTro", perm != null ? perm.getVaiTro() : "XEM");
                    areaData.put("inheritedFromProject", false);
                }
                
                areaList.add(areaData);
            }
            
            // Lấy tất cả thiết bị trong dự án
            List<ThietBi> allDevices = thietBiRepository.findByKhuVucDuAnMaDuAn(duAnId);
            List<PhanQuyenThietBi> userDevicePerms = phanQuyenThietBiRepository.findByMaNguoiDungAndMaDuAn(userId, duAnId);
            
            Map<Long, PhanQuyenThietBi> devicePermMap = new HashMap<>();
            for (PhanQuyenThietBi perm : userDevicePerms) {
                devicePermMap.put(perm.getThietBi().getMaThietBi(), perm);
            }
            
            List<Map<String, Object>> deviceList = new ArrayList<>();
            for (ThietBi device : allDevices) {
                Map<String, Object> deviceData = new HashMap<>();
                deviceData.put("maThietBi", device.getMaThietBi());
                deviceData.put("tenThietBi", device.getTenThietBi());
                deviceData.put("tenKhuVuc", device.getKhuVuc().getTenKhuVuc());
                
                // CHU_SO_HUU và QUAN_LY: tự động có full quyền thiết bị, không chỉnh được
                // Chỉ NGUOI_DUNG mới cần phân quyền chi tiết
                PhanQuyenThietBi perm = devicePermMap.get(device.getMaThietBi());
                
                if (hasFullAccess) {
                    // CHU_SO_HUU và QUAN_LY: tự động full quyền, không chỉnh được
                    deviceData.put("hasPermission", true);
                    deviceData.put("coQuyenXemDuLieu", true);
                    deviceData.put("coQuyenDieuKhien", true);
                    deviceData.put("inheritedFromProject", true);
                } else {
                    // NGUOI_DUNG: phân quyền chi tiết
                    deviceData.put("hasPermission", perm != null);
                    deviceData.put("coQuyenXemDuLieu", perm != null && Boolean.TRUE.equals(perm.getCoQuyenXemDuLieu()));
                    deviceData.put("coQuyenDieuKhien", perm != null && Boolean.TRUE.equals(perm.getCoQuyenDieuKhien()));
                    deviceData.put("inheritedFromProject", false);
                }
                
                deviceList.add(deviceData);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("areas", areaList);
            response.put("devices", deviceList);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Lưu phân quyền chi tiết (khu vực và thiết bị)
     */
    @PostMapping("/{duAnId}/thanh-vien/{userId}/permissions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveDetailPermissions(
            @PathVariable Long duAnId,
            @PathVariable Long userId,
            @RequestBody DetailPermissionsRequest request,
            Authentication auth) {
        
        try {
            String username = auth.getName();
            NguoiDung currentUser = nguoiDungRepository.findByTenDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            DuAn duAn = duAnRepository.findById(duAnId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));
            
            // Kiểm tra quyền quản lý
            if (!duAnAuthService.laQuanLyTroLen(duAn.getMaDuAn(), currentUser.getMaNguoiDung())) {
                return ResponseEntity.status(403).body(Map.of("success", false, "message", "Bạn không có quyền quản lý dự án này"));
            }
            
            NguoiDung targetUser = nguoiDungRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            
            // Kiểm tra vai trò của target user - chỉ cho phép phân quyền chi tiết cho NGUOI_DUNG
            DuAnRole targetUserRole = duAnAuthService.layVaiTroTrongDuAn(duAnId, userId);
            if (targetUserRole == DuAnRole.CHU_SO_HUU || targetUserRole == DuAnRole.QUAN_LY) {
                return ResponseEntity.status(400).body(Map.of("success", false, "message", "Không thể phân quyền chi tiết cho CHỦ SỞ HỮU hoặc QUẢN LÝ"));
            }
            
            // Gọi service để cập nhật phân quyền chi tiết
            phanQuyenService.capNhatPhanQuyenChiTiet(
                currentUser.getMaNguoiDung(),
                duAnId,
                targetUser,
                request.getAreaPermissions(),
                request.getDevicePermissions()
            );
            
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã lưu phân quyền thành công"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // DTOs
    static class MoiThanhVienRequest {
        private String email;
        private String vaiTro;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getVaiTro() { return vaiTro; }
        public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    }

    static class CapNhatVaiTroRequest {
        private String vaiTro;

        public String getVaiTro() { return vaiTro; }
        public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    }
    
    static class DetailPermissionsRequest {
        private List<AreaPermissionDTO> areaPermissions;
        private List<DevicePermissionDTO> devicePermissions;
        
        public List<AreaPermissionDTO> getAreaPermissions() { return areaPermissions; }
        public void setAreaPermissions(List<AreaPermissionDTO> areaPermissions) { this.areaPermissions = areaPermissions; }
        public List<DevicePermissionDTO> getDevicePermissions() { return devicePermissions; }
        public void setDevicePermissions(List<DevicePermissionDTO> devicePermissions) { this.devicePermissions = devicePermissions; }
    }
}
