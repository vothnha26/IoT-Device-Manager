package com.iot.management.controller.api.user;

import com.iot.management.controller.ControllerHelper;
import com.iot.management.model.entity.KhuVuc;
import com.iot.management.service.KhuVucService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/locations", "/api/regions"})  // Hỗ trợ cả hai endpoint
// @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")  // Tạm thời bỏ kiểm tra quyền
public class KhuVucController {

    private final KhuVucService khuVucService;
    private final ControllerHelper controllerHelper;

    public KhuVucController(KhuVucService khuVucService, ControllerHelper controllerHelper) {
        this.khuVucService = khuVucService;
        this.controllerHelper = controllerHelper;
    }

    @GetMapping
    public ResponseEntity<List<KhuVuc>> getMyRootLocations(Principal principal) {
        Long userId = controllerHelper.getUserIdFromPrincipal(principal);
        List<KhuVuc> locations = khuVucService.findRootLocationsByOwner(userId);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<KhuVuc>> getChildLocations(@PathVariable Long parentId, Principal principal) {
        // TODO: Cần kiểm tra quyền sở hữu của khu vực cha
        List<KhuVuc> childLocations = khuVucService.findChildLocations(parentId);
        return ResponseEntity.ok(childLocations);
    }
    
    @PostMapping
    public ResponseEntity<KhuVuc> createLocation(@RequestBody KhuVuc khuVuc, @RequestParam(required = false) Long parentId, Principal principal) {
        Long userId = controllerHelper.getUserIdFromPrincipal(principal);
        KhuVuc createdLocation = khuVucService.createLocation(userId, parentId, khuVuc);
        return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuVuc> updateLocation(@PathVariable Long id, @RequestBody KhuVuc khuVuc, Principal principal) {
        Long userId = controllerHelper.getUserIdFromPrincipal(principal);
        khuVuc.setMaKhuVuc(id); // Đảm bảo ID khớp với path variable
        KhuVuc updatedLocation = khuVucService.updateLocation(userId, khuVuc);
        return ResponseEntity.ok(updatedLocation);
    }
}