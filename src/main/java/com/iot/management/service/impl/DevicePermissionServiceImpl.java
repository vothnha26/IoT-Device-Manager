package com.iot.management.service.impl;

import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.UserDevicePermission;
import com.iot.management.repository.NguoiDungRepository;
import com.iot.management.repository.ThietBiRepository;
import com.iot.management.repository.UserDevicePermissionRepository;
import com.iot.management.service.DevicePermissionService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DevicePermissionServiceImpl implements DevicePermissionService {

    private final UserDevicePermissionRepository permissionRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThietBiRepository deviceRepository;

    public DevicePermissionServiceImpl(UserDevicePermissionRepository permissionRepository,
                                       NguoiDungRepository nguoiDungRepository,
                                       ThietBiRepository deviceRepository) {
        this.permissionRepository = permissionRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public List<ThietBi> getNguoiDungAccessibleDevices(Long nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        return permissionRepository.findAllDevicesByNguoiDung(nguoiDung);
    }

    @Override
    @Transactional
    public void grantDeviceAccess(Long nguoiDungId, Long deviceId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        ThietBi device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại"));
        
        if (!permissionRepository.existsByNguoiDungAndDevice(nguoiDung, device)) {
            NguoiDung currentNguoiDung = getCurrentNguoiDung();
            
            UserDevicePermission permission = new UserDevicePermission();
            permission.setNguoiDung(nguoiDung);
            permission.setDevice(device);
            permission.setCreatedAt(LocalDateTime.now());
            permission.setCreatedBy(currentNguoiDung);
            
            permissionRepository.save(permission);
        }
    }

    @Override
    @Transactional
    public void revokeDeviceAccess(Long nguoiDungId, Long deviceId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        ThietBi device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại"));
        
        permissionRepository.deleteByNguoiDungAndDevice(nguoiDung, device);
    }

    @Override
    public boolean hasDeviceAccess(Long nguoiDungId, Long deviceId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        ThietBi device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Thiết bị không tồn tại"));
        
        return permissionRepository.existsByNguoiDungAndDevice(nguoiDung, device);
    }

    @Override
    public List<ThietBi> getAllDevices() {
        return deviceRepository.findAll();
    }

    private NguoiDung getCurrentNguoiDung() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng hiện tại"));
    }
}