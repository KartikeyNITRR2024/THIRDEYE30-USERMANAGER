package com.thirdeye3.usermanager.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodePassword {
    public static void main(String[] args) {

        // Password you want to encode
        String rawPassword = "CHeck123";

        // Create encoder object
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Encode the password
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Print encoded password
        System.out.println("Raw Password    : " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        String inDB = "$2a$10$K6N6uqhsBhZWd.okS75iFu9wATNdv6qF9M5F2ujDCgzwCoNEPdgVO";
        System.out.println("In DB Password: " + inDB);
     }
}

