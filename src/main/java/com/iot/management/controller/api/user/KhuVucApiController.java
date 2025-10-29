package com.iot.management.controller.api.user;

import com.iot.management.model.entity.KhuVuc;
import com.iot.management.service.KhuVucService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khu-vuc")
public class KhuVucApiController {

    private final KhuVucService khuVucService;

    public KhuVucApiController(KhuVucService khuVucService) {
        this.khuVucService = khuVucService;
    }

    @GetMapping("/du-an/{maDuAn}")
    public ResponseEntity<List<KhuVuc>> getKhuVucByDuAn(@PathVariable Long maDuAn) {
        List<KhuVuc> khuVucs = khuVucService.findByDuAn(maDuAn);
        return ResponseEntity.ok(khuVucs);
    }
}
