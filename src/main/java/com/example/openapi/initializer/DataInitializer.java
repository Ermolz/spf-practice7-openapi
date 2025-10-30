package com.example.openapi.initializer;

import com.example.openapi.entity.Role;
import com.example.openapi.entity.User;
import com.example.openapi.repository.RoleRepository;
import com.example.openapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner init(UserRepository userRepo, RoleRepository roleRepo) {
        return args -> {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_ADMIN").build()));
            Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepo.save(Role.builder().name("ROLE_USER").build()));

            if (userRepo.findByUsername("admin").isEmpty()) {
                userRepo.save(User.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .roles(Set.of(adminRole, userRole))
                        .build());
            }

            if (userRepo.findByUsername("user").isEmpty()) {
                userRepo.save(User.builder()
                        .username("user")
                        .password(encoder.encode("user123"))
                        .roles(Set.of(userRole))
                        .build());
            }
        };
    }
}