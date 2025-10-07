package com.iot.management.service.impl;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.model.entity.VaiTro;
import com.iot.management.model.repository.NguoiDungRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class NguoiDungDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public NguoiDungDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung user = nguoiDungRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Collection<GrantedAuthority> authorities = user.getVaiTro().stream()
            .map((VaiTro role) -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getMatKhauBam())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getKichHoat())
            .build();
    }
}
