package com.autolitsenziya.api.config;

import com.autolitsenziya.api.entity.Role;
import com.autolitsenziya.api.entity.User;
import com.autolitsenziya.api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    public AdminSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminPhone = "+998505701920";
        if (!userRepository.existsByPhone(adminPhone)) {
            User admin = User.builder()
                    .phone(adminPhone)
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("[ADMIN SEEDER] Created superadmin with phone: " + adminPhone);
        } else {
            userRepository.findByPhone(adminPhone).ifPresent(user -> {
                if (user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    userRepository.save(user);
                    System.out.println("[ADMIN SEEDER] Updated user role to ADMIN for phone: " + adminPhone);
                }
            });
            System.out.println("[ADMIN SEEDER] Superadmin already exists with phone: " + adminPhone);
        }
    }
}
