package com.iot.management.repository;

import com.iot.management.model.entity.VaiTroQuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaiTroQuyenRepository extends JpaRepository<VaiTroQuyen, VaiTroQuyen.VaiTroQuyenId> {
    
    @Query("SELECT vq FROM VaiTroQuyen vq WHERE vq.maVaiTro = :maVaiTro")
    List<VaiTroQuyen> findByMaVaiTro(@Param("maVaiTro") Long maVaiTro);
    
    @Query("SELECT vq FROM VaiTroQuyen vq WHERE vq.maQuyen = :maQuyen")
    List<VaiTroQuyen> findByMaQuyen(@Param("maQuyen") Long maQuyen);
    
    @Query("DELETE FROM VaiTroQuyen vq WHERE vq.maVaiTro = :maVaiTro")
    void deleteByMaVaiTro(@Param("maVaiTro") Long maVaiTro);
}
