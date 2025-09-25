package com.thirdeye3.usermanager.services.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.entities.Role;
import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;
import com.thirdeye3.usermanager.exceptions.UserNotFoundException;
import com.thirdeye3.usermanager.services.AuthService;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.repositories.UserRepository;
import com.thirdeye3.usermanager.repositories.RoleRepository;
import com.thirdeye3.usermanager.utils.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {
	
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${thirdeye.jwt.token.starter}")
    private String tokenStarter;
    

    @Override
    public String register(AuthUserPayload authUserPayload) {
        logger.info("Attempting to register new user with username: {}", authUserPayload.getUserName());
        
        if(userRepository.countByActiveTrue() >= propertyService.getMaximumNoOfUsers())
        {
        	throw new UserNotFoundException("Maximum number of users reached.");
        }

        if (userRepository.findByUserName(authUserPayload.getUserName()).isPresent()) {
            logger.warn("Registration failed: User already exists with username: {}", authUserPayload.getUserName());
            throw new UserNotFoundException("User already present with username " + authUserPayload.getUserName());
        }

        User user = new User();
        user.setUserName(authUserPayload.getUserName());
        user.setPassword(passwordEncoder.encode(authUserPayload.getPassword()));
        user.setActive(true);
        user.setFirstLogin(true);

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    logger.debug("ROLE_USER not found, creating new one");
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("User registered successfully with username: {}", user.getUserName());
        return "User registered successfully";
    }

    @Override
    public LoginResponsePayload login(AuthUserPayload authUserPayload) {
        logger.info("Attempting login for username: {}", authUserPayload.getUserName());

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authUserPayload.getUserName(),
                            authUserPayload.getPassword()
                    )
            );
            logger.debug("Authentication passed for username: {}", authUserPayload.getUserName());
        } catch (Exception ex) {
            logger.error("Login failed for username: {} - Invalid credentials", authUserPayload.getUserName());
            throw new UserNotFoundException("Invalid username and password");
        }

        User user = userRepository.findByUserName(authUserPayload.getUserName())
                .orElseThrow(() -> {
                    logger.error("User not found in DB after successful authentication: {}", authUserPayload.getUserName());
                    return new UserNotFoundException("Invalid username and password");
                });

        if (!Boolean.TRUE.equals(user.getActive())) {
            logger.warn("Login attempt failed: User {} is inactive", user.getUserName());
            throw new UserNotFoundException("User account is inactive. Please contact support.");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = tokenStarter+jwtUtil.generateToken(user.getUserName(), user.getUserId(), roles);
        logger.info("User logged in successfully: {}", user.getUserName());
        logger.debug("Generated JWT token for user {} with roles {}", user.getUserName(), roles);
        return new LoginResponsePayload(token, user.getUserName(), user.getFirstName(), user.getLastName(), roles, user.getFirstLogin());
    }
}
