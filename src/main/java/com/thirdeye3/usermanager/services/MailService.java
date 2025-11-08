package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.entities.Mail;
import com.thirdeye3.usermanager.entities.User;

public interface MailService {

	Mail updateOrCreateMail(Integer type, User user);

	Mail verifyOtp(Long mailId, Integer type, String otp);

	void deleteMails();

}
