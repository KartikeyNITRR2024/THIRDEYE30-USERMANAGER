package com.thirdeye3.usermanager.utils;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OtpGenerater {

    @Value("${thirdeye.otp.length}")
    private int otpLength;

    private static final SecureRandom RNG = new SecureRandom();

    public String generateOtp() {
        StringBuilder otp = new StringBuilder();
        otp.append(RNG.nextInt(9) + 1);
        for (int i = 1; i < otpLength; i++) {
            otp.append(RNG.nextInt(10));
        }
        return otp.toString();
    }
}


