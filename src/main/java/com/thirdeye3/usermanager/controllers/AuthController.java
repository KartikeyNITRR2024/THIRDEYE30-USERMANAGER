package com.thirdeye3.usermanager.controllers;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;
import com.thirdeye3.usermanager.dtos.RegisterResponsePayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordPayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordResponsePayload;
import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ValidateOtpPayload;
import com.thirdeye3.usermanager.dtos.ValidateOtpResponsePayload;
import com.thirdeye3.usermanager.services.AuthService;

@RestController
@RequestMapping("/um/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public Response<RegisterResponsePayload> register(@Valid @RequestBody AuthUserPayload authUserPayload) {
        logger.info("Going to create user with creds {}", authUserPayload);
        return new Response<>(true, 0, null, authService.register(authUserPayload));
    }
    
    @PostMapping("/login")
    public Response<LoginResponsePayload> login(@Valid @RequestBody AuthUserPayload authUserPayload) {
        logger.info("Going to login for user with creds {}", authUserPayload);
        return new Response<>(true, 0, null, authService.login(authUserPayload));
    }
    
    @PostMapping("/verifyotp")
    public Response<ValidateOtpResponsePayload> verifyOtp(@Valid @RequestBody ValidateOtpPayload validateOtpPayload) {
        logger.info("Going to validate otp for creds {}", validateOtpPayload);
        return new Response<>(true, 0, null, authService.validateOtp(validateOtpPayload));
    }
    
    @PostMapping("/resetpassword")
    public Response<ResetPasswordResponsePayload> resetpassword(@Valid @RequestBody ResetPasswordPayload resetPasswordPayload) {
        logger.info("Going to reset password for user creds {}", resetPasswordPayload);
        return new Response<>(true, 0, null, authService.resetPassword(resetPasswordPayload));
    }
    
    
    
}