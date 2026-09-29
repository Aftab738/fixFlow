package com.maintenance.fixFlow.config;

import com.maintenance.fixFlow.entity.Role;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${fixflow.manager.email}")
    private String managerEmail;

    @Value("${fixflow.manager.password}")
    private String managerPassword;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail(managerEmail).isEmpty()) {

            User user = new User();

            user.setName("FixFlow Manager");
            user.setEmail(managerEmail);
            user.setPassword(passwordEncoder.encode(managerPassword));
            user.setPhone("9999999999");
            user.setRole(Role.MANAGER);
            user.setUnit(null);

            userRepository.save(user);
        }
    }
}