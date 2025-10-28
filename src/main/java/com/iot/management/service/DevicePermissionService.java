package com.iot.management.service;

import com.iot.management.model.entity.ThietBi;
import java.util.List;

public interface DevicePermissionService {

   List<ThietBi> getNguoiDungAccessibleDevices(Long nguoiDungId);

    void grantDeviceAccess(Long nguoiDungId, Long deviceId);

    void revokeDeviceAccess(Long nguoiDungId, Long deviceId);

    boolean hasDeviceAccess(Long nguoiDungId, Long deviceId);
    
    List<ThietBi> getAllDevices();
}