package com.thirdeye3.usermanager.services.impl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.entities.Role;
import com.thirdeye3.usermanager.entities.Mail;
import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;
import com.thirdeye3.usermanager.dtos.RegisterResponsePayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordPayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordResponsePayload;
import com.thirdeye3.usermanager.dtos.ValidateOtpPayload;
import com.thirdeye3.usermanager.dtos.ValidateOtpResponsePayload;
import com.thirdeye3.usermanager.exceptions.UserNotFoundException;
import com.thirdeye3.usermanager.services.AuthService;
import com.thirdeye3.usermanager.services.MailService;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.repositories.UserRepository;
import com.thirdeye3.usermanager.repositories.MailRepository;
import com.thirdeye3.usermanager.repositories.RoleRepository;
import com.thirdeye3.usermanager.utils.JwtUtil;
import com.thirdeye3.usermanager.utils.OtpGenerater;
import com.thirdeye3.usermanager.utils.TimeManager;

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

    @Autowired
    private OtpGenerater otpGenerater;

    @Autowired
    private MailService mailService;

    @Value("${thirdeye.jwt.token.starter}")
    private String tokenStarter;

    @Override
    public RegisterResponsePayload register(AuthUserPayload authUserPayload) {
        logger.info("Register request received for username: {}", authUserPayload.getUserName());

        if (userRepository.countByActiveTrue() >= propertyService.getMaximumNoOfUsers()) {
            logger.warn("User limit reached. Registration blocked.");
            throw new UserNotFoundException("Maximum number of users reached.");
        }

        if (userRepository.findByUserName(authUserPayload.getUserName()).isPresent()) {
            logger.warn("Registration failed. Username already exists: {}", authUserPayload.getUserName());
            throw new UserNotFoundException("User already exists");
        }

        User user = new User();
        user.setUserName(authUserPayload.getUserName());
        user.setPassword(passwordEncoder.encode(authUserPayload.getPassword()));
        user.setActive(true);
        user.setFirstLogin(true);
        user.setEmailVerified(false);

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    logger.info("ROLE_USER not found. Creating default role.");
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        user.getRoles().add(role);
        User newUser = userRepository.save(user);
        logger.info("User created successfully: {}", newUser.getUserName());

        Mail mail = mailService.updateOrCreateMail(1, newUser);
        logger.info("OTP generated and stored for userId: {}", newUser.getUserId());
        
        String maskedUserName = user.getUserName();
        if (user.getUserName().length() > 5) {
        	maskedUserName = user.getUserName().substring(0, 5) + "****************";
        }

        return new RegisterResponsePayload(mail.getId(), mail.getMailType(), "User created. Verification OTP sent to email : " + maskedUserName);
    }
    
    @Override
    public ResetPasswordResponsePayload resetPassword(ResetPasswordPayload resetPasswordPayload) {

        logger.info("Reset password request received. Attempting to find user using username: {} or phone: {}",
                resetPasswordPayload.getUserName(), resetPasswordPayload.getPhoneNumber());

        Optional<User> userOpt = userRepository.findByUserNameOrPhoneNumber(
                resetPasswordPayload.getUserName(),
                resetPasswordPayload.getPhoneNumber()
        );

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("User found with userId: {} and username: {}", user.getUserId(), user.getUserName());
            Mail mail = mailService.updateOrCreateMail(2, user);
            String maskedUserName = user.getUserName();
            if (user.getUserName().length() > 5) {
            	maskedUserName = user.getUserName().substring(0, 5) + "****************";
            }
            logger.info("OTP generated for password reset. MailId: {} | Masked User: {}", mail.getId(), maskedUserName);
            return new ResetPasswordResponsePayload(
                    mail.getId(),
                    2,
                    true,
                    "OTP sent to email : " + maskedUserName
            );
        }
        logger.warn("Reset password failed: No user found with provided username/phone.");
        return new ResetPasswordResponsePayload(null, null, false, "User not found with given credentials.");
    }


    @Override
    public ValidateOtpResponsePayload validateOtp(ValidateOtpPayload payload) {

        logger.info("Validating OTP for mailId: {}, mailType: {}", payload.getMailId(), payload.getMailType());

        Mail mail = mailService.verifyOtp(payload.getMailId(), payload.getMailType(), payload.getOtp());
        boolean success = false;
        String message = "Invalid or expired OTP.";

        if (mail == null) {
            logger.warn("OTP validation failed for mailId: {}", payload.getMailId());
            return new ValidateOtpResponsePayload(payload.getMailType(), false, message);
        }

        Optional<User> userOpt = userRepository.findById(mail.getUserId());
        if (userOpt.isEmpty()) {
            logger.error("User associated with mailId {} not found", payload.getMailId());
            return new ValidateOtpResponsePayload(payload.getMailType(), false, "User not found.");
        }

        User user = userOpt.get();

        if (payload.getMailType() == 1) {
            user.setEmailVerified(true);
            userRepository.save(user);
            success = true;
            message = "Email verification successful.";
            logger.info("Email verified for userId: {}", user.getUserId());
        } 
        else if (payload.getMailType() == 2) {
            user.setPassword(passwordEncoder.encode(payload.getNewPassword()));
            userRepository.save(user);
            success = true;
            message = "Password reset successful. Please login again.";
            logger.info("Password reset successful for userId: {}", user.getUserId());
        }

        return new ValidateOtpResponsePayload(payload.getMailType(), success, message);
    }


    @Override
    public LoginResponsePayload login(AuthUserPayload authUserPayload) {
        logger.info("Login attempt for username: {}", authUserPayload.getUserName());

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authUserPayload.getUserName(), authUserPayload.getPassword()));
        } catch (Exception ex) {
            logger.warn("Login failed due to invalid credentials for username: {}", authUserPayload.getUserName());
            throw new UserNotFoundException("Invalid username or password");
        }

        User user = userRepository.findByUserName(authUserPayload.getUserName())
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));
        
        
        

        if (!user.getEmailVerified()) {
            logger.warn("Login blocked. Email not verified for username: {}", user.getUserName());
            Mail mail = mailService.updateOrCreateMail(1, user);
            
            String maskedUserName = user.getUserName();
            if (user.getUserName().length() > 5) {
            	maskedUserName = user.getUserName().substring(0, 5) + "****************";
            }
            
            return new LoginResponsePayload(null, null, null, null, null, null, mail.getId(), 1, "Email not verified. OTP sent to email associated with email: " + maskedUserName, false, 2L);
        }

        if (!user.getActive()) {
            logger.warn("Login blocked. Account inactive for username: {}", user.getUserName());
            return new LoginResponsePayload(null, null, null, null, null, null, null, null, "Account inactive.", false, 1L);
        }

        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        String token = tokenStarter + jwtUtil.generateToken(user.getUserName(), user.getUserId(), roles);

        logger.info("Login successful for username: {}", user.getUserName());
        return new LoginResponsePayload(token, user.getUserId(), user.getUserName(), user.getFirstName(), roles, user.getFirstLogin(), null, null, "Login successful.", true, null);
    }
}
