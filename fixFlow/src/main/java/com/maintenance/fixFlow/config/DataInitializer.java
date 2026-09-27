package com.maintenance.fixFlow.config;

import com.maintenance.fixFlow.entity.Role;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("manager@fixflow.com").isEmpty()) {

            User user = new User();

            user.setName("FixFlow Manager");
            user.setEmail("manager@fixflow.com");
            user.setPassword(passwordEncoder.encode("Manager@123"));
            user.setPhone("9999999999");
            user.setRole(Role.MANAGER);
            user.setUnit(null);

            userRepository.save(user);
        }
    }
}