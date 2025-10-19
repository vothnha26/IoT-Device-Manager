package com.iot.management.security;

import com.iot.management.model.entity.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class SecurityUser implements UserDetails {
    private NguoiDung nguoiDung;

    public SecurityUser(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return nguoiDung.getVaiTro().stream()
                .map(role -> new SimpleGrantedAuthority(role.getTenVaiTro()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return nguoiDung.getMatKhauBam();
    }

    @Override
    public String getUsername() {
        return nguoiDung.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Handle null as false for safety
        return nguoiDung.getKichHoat() != null && nguoiDung.getKichHoat();
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }
    
    /**
     * Helper method để lấy ID người dùng
     */
    public Long getMaNguoiDung() {
        return nguoiDung != null ? nguoiDung.getMaNguoiDung() : null;
    }
}