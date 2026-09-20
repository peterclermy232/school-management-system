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
        // Seed any role that doesn't exist yet, per-role rather than an all-or-nothing count
        // check, so adding a new ERole value still gets backfilled on an already-seeded
        // (e.g. already-deployed) database instead of being silently skipped.
        for (ERole role : ERole.values()) {
            if (roleRepository.findByName(role).isEmpty()) {
                roleRepository.save(new Role(role));
                logger.info("Seeded missing role: {}", role);
            }
        }
    }
}
