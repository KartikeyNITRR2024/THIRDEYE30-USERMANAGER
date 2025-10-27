package com.thirdeye3.usermanager.services;

import java.util.Map;

public interface EmailService {
    void sendZipToEmail(String to, String subject, String messageBody,
            String zipFileName, byte[] zipData);
}
