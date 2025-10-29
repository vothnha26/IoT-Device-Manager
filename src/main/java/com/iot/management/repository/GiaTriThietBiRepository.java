package com.iot.management.repository;
import com.iot.management.model.entity.CauHinhTruongDuLieu;
import com.iot.management.model.entity.GiaTriThietBi;
import com.iot.management.model.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface GiaTriThietBiRepository extends JpaRepository<GiaTriThietBi, Long> {
    Optional<GiaTriThietBi> findByThietBiAndCauHinhTruongDuLieu(ThietBi thietBi, CauHinhTruongDuLieu cauHinhTruongDuLieu);

    // Lấy tất cả giá trị hiện tại của một thiết bị
    List<GiaTriThietBi> findByThietBi(ThietBi thietBi);

    // Kiểm tra tồn tại giá trị hiện tại của thiết bị theo trường
    boolean existsByThietBiAndCauHinhTruongDuLieu(ThietBi thietBi, CauHinhTruongDuLieu cauHinhTruongDuLieu);

}
