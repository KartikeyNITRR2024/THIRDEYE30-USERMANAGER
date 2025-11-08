package com.thirdeye3.usermanager.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye3.usermanager.entities.Mail;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.repositories.MailRepository;
import com.thirdeye3.usermanager.services.MailService;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.utils.OtpGenerater;
import com.thirdeye3.usermanager.utils.TimeManager;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private OtpGenerater otpGenerater;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private TimeManager timeManager;

    @Override
    public Mail updateOrCreateMail(Integer type, User user) {
        logger.info("Checking existing OTP for userId={}, mailType={}", user.getUserId(), type);

        Optional<Mail> opt = mailRepository.checkOtp(user.getUserId(), type, timeManager.getLocalCurrentTime());

        if (opt.isPresent()) {
            logger.info("Existing valid OTP found. Updating record for userId={}", user.getUserId());
            Mail mail = opt.get();
            mail.setNoOFTriesLeft(propertyService.getMaximumNoOfTriesToSendOtp());
            mail.setExpiryTime(timeManager.getLocalCurrentTime()
                    .plusMinutes(propertyService.getOtpExpiryTimeInMinutes()));
            Mail saved = mailRepository.save(mail);
            logger.info("OTP expiry extended and retries reset. MailId={}", saved.getId());
            return saved;
        }

        logger.info("No valid OTP exists. Creating new OTP for userId={}", user.getUserId());
        Mail mail = new Mail();
        mail.setUserId(user.getUserId());
        mail.setUserName(user.getUserName());
        mail.setMailType(type);
        mail.setOtp(otpGenerater.generateOtp());
        mail.setSuccess(false);
        mail.setNoOFTriesLeft(propertyService.getMaximumNoOfTriesToSendOtp());
        mail.setExpiryTime(timeManager.getLocalCurrentTime()
                .plusMinutes(propertyService.getOtpExpiryTimeInMinutes()));
        
        Mail saved = mailRepository.save(mail);
        logger.info("New OTP generated successfully. MailId={}, OTP={}", saved.getId(), saved.getOtp());
        return saved;
    }

    @Override
    public Mail verifyOtp(Long mailId, Integer type, String otp) {
        logger.info("Validating OTP for mailId={}, mailType={}", mailId, type);

        Optional<Mail> opt = mailRepository.findById(mailId);

        if (opt.isPresent()) {
            Mail mail = opt.get();
            boolean isCorrectType = mail.getMailType().equals(type);
            boolean isCorrectOtp = mail.getOtp().equals(otp);
            boolean isNotExpired = timeManager.getLocalCurrentTime().isBefore(mail.getExpiryTime());

            if (isCorrectType && isCorrectOtp && isNotExpired) {
                mailRepository.markSuccess(mailId);
                logger.info("✅ OTP verified successfully for userId={}", mail.getUserId());
                return mail;
            } else {
                logger.warn("❌ OTP validation failed for mailId={}. Reason: typeMatch={}, otpMatch={}, notExpired={}",
                        mailId, isCorrectType, isCorrectOtp, isNotExpired);
            }
        } else {
            logger.warn("⚠️ No Mail record found for mailId={}", mailId);
        }

        return null;
    }

    @Override
    public void deleteMails() {
        logger.info("🧹 Cleaning expired / used / failed OTP records...");
        mailRepository.deleteExpiredOrFailed(timeManager.getLocalCurrentTime());
        logger.info("✅ Cleanup completed.");
    }
}
