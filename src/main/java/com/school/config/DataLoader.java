package com.school.config;

import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_TEACHER));
            roleRepository.save(new Role(ERole.ROLE_STUDENT));
            roleRepository.save(new Role(ERole.ROLE_PARENT));
            logger.info("Roles initialized successfully!");
        }
    }
}