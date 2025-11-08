package com.thirdeye3.usermanager.services.impl;

import java.util.Optional;

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
        Optional<Mail> opt = mailRepository.checkOtp(user.getUserId(), type, timeManager.getLocalCurrentTime());

        if(opt.isPresent()) {
            Mail mail = opt.get();
            mail.setNoOFTriesLeft(propertyService.getMaximumNoOfTriesToSendOtp());
            mail.setExpiryTime(timeManager.getLocalCurrentTime()
                    .plusMinutes(propertyService.getOtpExpiryTimeInMinutes()));
            return mailRepository.save(mail);
        }

        Mail mail = new Mail();
        mail.setUserId(user.getUserId());
        mail.setUserName(user.getUserName());
        mail.setMailType(type);
        mail.setOtp(otpGenerater.generateOtp());
        mail.setSuccess(false);
        mail.setNoOFTriesLeft(propertyService.getMaximumNoOfTriesToSendOtp());
        mail.setExpiryTime(timeManager.getLocalCurrentTime()
                .plusMinutes(propertyService.getOtpExpiryTimeInMinutes()));
        return mailRepository.save(mail);
    }

    @Override
    public Mail verifyOtp(Long mailId, Integer type, String otp) {
        Optional<Mail> opt = mailRepository.findById(mailId);
        if(opt.isPresent()) {
            Mail mail = opt.get();
            boolean isCorrectType = mail.getMailType().equals(type);
            boolean isCorrectOtp = mail.getOtp().equals(otp);
            boolean isNotExpired = timeManager.getLocalCurrentTime().isBefore(mail.getExpiryTime());
            if(isCorrectType && isCorrectOtp && isNotExpired) {
                mailRepository.markSuccess(mailId);
                return mail;
            }
        }
        return null;
    }
}
