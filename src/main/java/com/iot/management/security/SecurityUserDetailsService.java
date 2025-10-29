package com.iot.management.security;

import com.iot.management.model.entity.NguoiDung;
import com.iot.management.repository.NguoiDungRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SecurityUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public SecurityUserDetailsService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + email));
            
        return new SecurityUser(nguoiDung);
    }
}