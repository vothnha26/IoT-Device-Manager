package com.iot.management.repository;

import com.iot.management.model.entity.ThietBi;
import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.UserDevicePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDevicePermissionRepository extends JpaRepository<UserDevicePermission, Long> {
    
    List<UserDevicePermission> findByNguoiDung(NguoiDung nguoiDung);
    
    List<UserDevicePermission> findByDevice(ThietBi device);
    
    boolean existsByNguoiDungAndDevice(NguoiDung nguoiDung, ThietBi device);
    
    @Query("SELECT udp.device FROM UserDevicePermission udp WHERE udp.nguoiDung = ?1")
    List<ThietBi> findAllDevicesByNguoiDung(NguoiDung nguoiDung);
    
    void deleteByNguoiDungAndDevice(NguoiDung nguoiDung, ThietBi device);
}