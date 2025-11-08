package com.thirdeye3.usermanager.configs;

import com.thirdeye3.usermanager.entities.Role;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.repositories.UserRepository;
import com.thirdeye3.usermanager.repositories.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminUserConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserConfig.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${thirdeye.admin.username}")
    private String username;

    @Value("${thirdeye.admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_ADMIN");
                    Role saved = roleRepository.save(role);
                    log.info("Created default role: {}", saved.getName());
                    return saved;
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    Role saved = roleRepository.save(role);
                    log.info("Created default role: {}", saved.getName());
                    return saved;
                });

        if (userRepository.findByUserName(username).isEmpty()) {
            User admin = new User();
            admin.setUserName(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRoles(Set.of(adminRole, userRole));
            admin.setFirstLogin(true);
            admin.setEmailVerified(false);
            userRepository.save(admin);
            log.info("Admin user '{}' created successfully with roles {}", username, admin.getRoles());
        } else {
            log.info("Admin user '{}' already exists, skipping creation.", username);
        }
    }
}
