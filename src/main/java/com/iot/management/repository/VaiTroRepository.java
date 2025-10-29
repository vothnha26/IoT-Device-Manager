package com.iot.management.repository;

import com.iot.management.model.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {
    @Query("select v from VaiTro v where v.tenVaiTro = :name")
    Optional<VaiTro> findByName(@Param("name") String name);
}
