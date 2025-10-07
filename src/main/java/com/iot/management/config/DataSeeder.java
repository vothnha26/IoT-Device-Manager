package com.iot.management.config;

import com.iot.management.model.entity.VaiTro;
import com.iot.management.model.repository.VaiTroRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final VaiTroRepository vaiTroRepository;

    public DataSeeder(VaiTroRepository vaiTroRepository) {
        this.vaiTroRepository = vaiTroRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        // Only create two roles as requested: user and manager
        List<String> roleNames = Arrays.asList("USER", "MANAGER");
        for (String roleName : roleNames) {
            Optional<VaiTro> existingRole = vaiTroRepository.findByName(roleName);
            if (existingRole.isEmpty()) {
                VaiTro newRole = new VaiTro();
                newRole.setName(roleName);
                vaiTroRepository.save(newRole);
                System.out.println("Seeded role: " + roleName);
            }
        }
    }
}