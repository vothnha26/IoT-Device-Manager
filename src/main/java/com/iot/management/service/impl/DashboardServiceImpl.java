package com.iot.management.service.impl;

import com.iot.management.model.dto.DashboardStatsDTO;
import com.iot.management.model.dto.DeviceDTO;
import com.iot.management.model.dto.PackageUsageDTO;
import com.iot.management.model.dto.RoomDTO;
import com.iot.management.model.entity.*;
import com.iot.management.model.repository.DangKyGoiRepository;
import com.iot.management.model.repository.DuAnRepository;
import com.iot.management.model.repository.KhuVucRepository;
import com.iot.management.model.repository.NguoiDungRepository;
import com.iot.management.model.repository.NhatKyDuLieuRepository;
import com.iot.management.model.repository.PhanQuyenDuAnRepository;
import com.iot.management.model.repository.ThietBiRepository;
import com.iot.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ThietBiRepository thietBiRepository;

    @Autowired
    private KhuVucRepository khuVucRepository;

    @Autowired
    private NhatKyDuLieuRepository nhatKyDuLieuRepository;
    
    @Autowired
    private DuAnRepository duAnRepository;
    
    @Autowired
    private DangKyGoiRepository dangKyGoiRepository;
    
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    
    @Autowired
    private PhanQuyenDuAnRepository phanQuyenDuAnRepository;

    @Override
    public DashboardStatsDTO getDashboardStats(Long userId) {
        // Đếm tổng số khu vực của user
        Long totalKhuVuc = khuVucRepository.countByChuSoHuu_MaNguoiDung(userId);

        // Lấy tất cả thiết bị của user
        List<ThietBi> allDevices = thietBiRepository.findByChuSoHuu_MaNguoiDung(userId);
        Long totalThietBi = (long) allDevices.size();

        // Đếm theo nhóm thiết bị (sử dụng enum NhomThietBi)
        Long totalControllers = allDevices.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.CONTROLLER)
                .count();

        Long totalSensors = allDevices.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.SENSOR)
                .count();

        Long totalActuators = allDevices.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.ACTUATOR)
                .count();

        // Đếm thiết bị online/offline
        // Thiết bị được coi là "hoạt động" nếu có hoạt động trong 5 phút gần đây
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        Long online = allDevices.stream()
                .filter(d -> d.getLanHoatDongCuoi() != null && 
                            d.getLanHoatDongCuoi().isAfter(fiveMinutesAgo))
                .count();
        Long offline = totalThietBi - online;

        return new DashboardStatsDTO(totalKhuVuc, totalThietBi, totalControllers, 
                                    totalSensors, totalActuators, online, offline);
    }

    @Override
    public List<RoomDTO> getRoomsWithDevices(Long userId) {
        // Lấy tất cả khu vực của user
        List<KhuVuc> khuVucs = khuVucRepository.findByChuSoHuu_MaNguoiDung(userId);
        
        return khuVucs.stream()
                .map(this::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomDTO getRoomDetail(Long roomId) {
        KhuVuc khuVuc = khuVucRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khu vực"));
        
        return convertToRoomDTO(khuVuc);
    }

    /**
     * Convert KhuVuc entity sang RoomDTO
     */
    private RoomDTO convertToRoomDTO(KhuVuc khuVuc) {
        RoomDTO room = new RoomDTO();
        room.setMaKhuVuc(khuVuc.getMaKhuVuc());
        room.setTenKhuVuc(khuVuc.getTenKhuVuc());
        room.setLoaiKhuVuc(khuVuc.getLoaiKhuVuc());

        // Lấy danh sách thiết bị trong khu vực
        List<ThietBi> thietBis = thietBiRepository.findByKhuVuc_MaKhuVuc(khuVuc.getMaKhuVuc());
        
        // Đếm theo nhóm thiết bị trong khu vực này (sử dụng enum)
        Long controllerCount = thietBis.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.CONTROLLER)
                .count();
        room.setControllerCount(controllerCount);

        Long sensorCount = thietBis.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.SENSOR)
                .count();
        room.setSensorCount(sensorCount);

        Long actuatorCount = thietBis.stream()
                .filter(d -> d.getLoaiThietBi() != null && 
                            d.getLoaiThietBi().getNhomThietBi() == NhomThietBi.ACTUATOR)
                .count();
        room.setActuatorCount(actuatorCount);
        
        List<DeviceDTO> deviceDTOs = thietBis.stream()
                .map(this::convertToDeviceDTO)
                .collect(Collectors.toList());
        
        room.setDevices(deviceDTOs);

        // Tìm nhiệt độ và độ ẩm mới nhất từ các sensor trong phòng
        Double avgTemp = null;
        Double avgHumidity = null;
        LocalDateTime latestTime = null;

        for (ThietBi thietBi : thietBis) {
            if (thietBi.getLoaiThietBi() != null && 
                thietBi.getLoaiThietBi().getTenLoai().toLowerCase().contains("temperature")) {
                
                List<NhatKyDuLieu> latestData = nhatKyDuLieuRepository
                        .findTop1ByThietBi_MaThietBiOrderByThoiGianDesc(thietBi.getMaThietBi());
                
                if (!latestData.isEmpty()) {
                    NhatKyDuLieu data = latestData.get(0);
                    try {
                        avgTemp = Double.parseDouble(data.getGiaTri());
                        if (latestTime == null || data.getThoiGian().isAfter(latestTime)) {
                            latestTime = data.getThoiGian();
                        }
                    } catch (NumberFormatException e) {
                        // Skip if not a number
                    }
                }
            }
            
            if (thietBi.getLoaiThietBi() != null && 
                thietBi.getLoaiThietBi().getTenLoai().toLowerCase().contains("humidity")) {
                
                List<NhatKyDuLieu> latestData = nhatKyDuLieuRepository
                        .findTop1ByThietBi_MaThietBiOrderByThoiGianDesc(thietBi.getMaThietBi());
                
                if (!latestData.isEmpty()) {
                    NhatKyDuLieu data = latestData.get(0);
                    try {
                        avgHumidity = Double.parseDouble(data.getGiaTri());
                        if (latestTime == null || data.getThoiGian().isAfter(latestTime)) {
                            latestTime = data.getThoiGian();
                        }
                    } catch (NumberFormatException e) {
                        // Skip
                    }
                }
            }
        }

        room.setCurrentTemp(avgTemp);
        room.setCurrentHumidity(avgHumidity);
        
        if (latestTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            room.setLastUpdated(latestTime.format(formatter));
        }

        return room;
    }

    /**
     * Convert ThietBi entity sang DeviceDTO
     */
    private DeviceDTO convertToDeviceDTO(ThietBi thietBi) {
        DeviceDTO device = new DeviceDTO();
        device.setMaThietBi(thietBi.getMaThietBi());
        device.setTenThietBi(thietBi.getTenThietBi());
        device.setTrangThai(thietBi.getTrangThai());
        
        if (thietBi.getLoaiThietBi() != null) {
            device.setLoaiThietBi(thietBi.getLoaiThietBi().getTenLoai());
            
            // Xác định thiết bị có thể điều khiển không dựa trên nhóm
            NhomThietBi nhom = thietBi.getLoaiThietBi().getNhomThietBi();
            device.setIsControllable(
                nhom == NhomThietBi.CONTROLLER || nhom == NhomThietBi.ACTUATOR
            );
        }

        // Lấy giá trị hiện tại từ nhật ký dữ liệu mới nhất
        List<NhatKyDuLieu> latestData = nhatKyDuLieuRepository
                .findTop1ByThietBi_MaThietBiOrderByThoiGianDesc(thietBi.getMaThietBi());
        
        if (!latestData.isEmpty()) {
            device.setCurrentValue(latestData.get(0).getGiaTri());
        } else {
            device.setCurrentValue("N/A");
        }

        return device;
    }

    @Override
    public PackageUsageDTO getPackageUsage(Long userId) {
        // Lấy người dùng
        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        // Lấy gói cước hiện tại của user
        DangKyGoi dangKyGoi = dangKyGoiRepository.findByNguoiDung_MaNguoiDungAndTrangThai(userId, "ACTIVE")
                .orElse(null);
        
        if (dangKyGoi == null || dangKyGoi.getGoiCuoc() == null) {
            // Nếu không có gói, trả về giá trị mặc định (gói free)
            return new PackageUsageDTO(0, 10, 0, 5, 0, 1, 0, "Free");
        }
        
        GoiCuoc goiCuoc = dangKyGoi.getGoiCuoc();
        
        // Đếm số thiết bị đang sử dụng (tất cả thiết bị của user trong tất cả dự án)
        List<DuAn> duAns = duAnRepository.findByNguoiDung(nguoiDung);
        int deviceUsed = 0;
        int areaUsed = 0;
        
        // Đếm số người dùng duy nhất được phân quyền trong các dự án (bao gồm cả chủ dự án)
        Set<Long> uniqueUsers = new HashSet<>();
        uniqueUsers.add(userId); // Thêm chủ sở hữu vào danh sách
        
        for (DuAn duAn : duAns) {
            // Đếm khu vực
            areaUsed += duAn.getKhuVucs() != null ? duAn.getKhuVucs().size() : 0;
            
            // Đếm thiết bị trong tất cả khu vực
            if (duAn.getKhuVucs() != null) {
                for (KhuVuc khuVuc : duAn.getKhuVucs()) {
                    deviceUsed += khuVuc.getThietBis() != null ? khuVuc.getThietBis().size() : 0;
                }
            }
            
            // Đếm số người dùng được phân quyền (bao gồm tất cả thành viên)
            List<PhanQuyenDuAn> phanQuyens = phanQuyenDuAnRepository.findByDuAn(duAn);
            for (PhanQuyenDuAn pq : phanQuyens) {
                if (pq.getNguoiDung() != null) {
                    uniqueUsers.add(pq.getNguoiDung().getMaNguoiDung());
                }
            }
        }
        
        int userUsed = uniqueUsers.size();
        
        // Tính số ngày còn lại
        long daysLeft = 0;
        if (dangKyGoi.getNgayKetThuc() != null) {
            daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), dangKyGoi.getNgayKetThuc());
            if (daysLeft < 0) {
                daysLeft = 0;
            }
        }
        
        return new PackageUsageDTO(
            deviceUsed, 
            goiCuoc.getSlThietBiToiDa(), 
            areaUsed, 
            goiCuoc.getSlKhuVucToiDa(),
            userUsed,
            goiCuoc.getSlNguoiDungToiDa() != null ? goiCuoc.getSlNguoiDungToiDa() : 1,
            daysLeft, 
            goiCuoc.getTenGoi()
        );
    }
}
