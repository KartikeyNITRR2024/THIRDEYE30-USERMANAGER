package com.thirdeye3.usermanager.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.thirdeye3.usermanager.entities.Mail;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.repositories.MailRepository;
import com.thirdeye3.usermanager.services.MailService;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.utils.OtpGenerater;
import com.thirdeye3.usermanager.utils.TimeManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import jakarta.mail.internet.MimeMessage;

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
    
    @Value("${thirdeye.mail.apikey}")
    private String brevoApiKey;

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
    

//    @Override
//    public void sendOtp() {
//        List<Mail> mails = mailRepository.findAllBySendFalseAndNoOFTriesLeftGreaterThan(0);
//        for (Mail mail : mails) {
//            try {
//                MimeMessage mimeMessage = mailSender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
//                String htmlBody =
//                        "<div style='font-family: Arial, sans-serif; padding: 20px;'>";
//				                if(mail.getMailType() == 1)
//				                {
//				                	htmlBody+= "<h2 style='color:#000;'>Verify Email OTP</h2>";
//				                }
//				                else if(mail.getMailType() == 2)
//				                {
//				                	htmlBody+= "<h2 style='color:#000;'>Recover password OTP</h2>";
//				                }
//			         htmlBody+= 
//                        "<p>Hello,</p>"
//                                + "<p>Your One-Time Password ";
//                                if(mail.getMailType() == 1)
//                                {
//                                	htmlBody+= "to verify email";
//                                }
//                                else if(mail.getMailType() == 2)
//                                {
//                                	htmlBody+= "to recover password";
//                                }
//                      htmlBody+= 
//                    	" is:</p>"
//                                + "<div style='font-size: 32px; font-weight: bold; padding: 10px; "
//                                + "background: #f2f2f2; width: fit-content; border-radius: 8px;'>"
//                                + mail.getOtp()
//                                + "</div>"
//                                + "<p>This OTP will expire in "+propertyService.getOtpExpiryTimeInMinutes()+" minutes.</p>"
//                                + "<p>If you did not request this, please ignore this email.</p>"
//                                + "<br/>"
//                                + "<p>Regards,<br/>Thirdeye Team</p>"
//                                + "</div>";
//                helper.setTo(mail.getUserName());
//                if(mail.getMailType() == 1)
//                {
//                	helper.setSubject("Verify email - Thirdeye");
//                	helper.setFrom("thirdeye-verify-email@thirdeye3.com");
//                }
//                else if(mail.getMailType() == 2)
//                {
//                	helper.setSubject("Recover password - ThirdEye");
//                	helper.setFrom("recover-password@thirdeye3.com");
//                }
//                helper.setText(htmlBody, true);
//                mailSender.send(mimeMessage);
//                mailRepository.markSend(mail.getId());
//                logger.info("HTML Mail sent successfully to: {}", mail.getUserName());
//            } catch (Exception e) {
//                logger.error("FAILED to send mail to: {}", mail.getUserName());
//                logger.error("Reason: {}", e.getMessage());
//                e.printStackTrace();
//            }
//            mailRepository.decreaseTries(mail.getId());
//        }
//    }
    
    @Override
    public void sendOtp() {
    	Long count = mailRepository.countBySendFalseAndNoOFTriesLeftGreaterThan(0);
    	if(count==0)
    	{
    		return;
    	}
    	logger.info("Sending Otp to {} users",count);
        List<Mail> mails = mailRepository.findAllBySendFalseAndNoOFTriesLeftGreaterThan(0);
        

        for (Mail mail : mails) {
            try {
                StringBuilder htmlBody = new StringBuilder();
                htmlBody.append("<div style='font-family: Arial, sans-serif; padding: 20px;'>");

                if (mail.getMailType() == 1) {
                    htmlBody.append("<h2 style='color:#000;'>Verify Email OTP</h2>");
                } else if (mail.getMailType() == 2) {
                    htmlBody.append("<h2 style='color:#000;'>Recover Password OTP</h2>");
                }

                htmlBody.append("<p>Hello,</p>");
                htmlBody.append("<p>Your One-Time Password ");

                if (mail.getMailType() == 1) {
                    htmlBody.append("to verify email");
                } else {
                    htmlBody.append("to recover password");
                }

                htmlBody.append(" is:</p>");

                htmlBody.append("<div style='font-size: 32px; font-weight: bold; padding: 10px; "
                        + "background: #f2f2f2; width: fit-content; border-radius: 8px;'>"
                        + mail.getOtp() + "</div>");

                htmlBody.append("<p>This OTP will expire in ")
                        .append(propertyService.getOtpExpiryTimeInMinutes())
                        .append(" minutes.</p>");

                htmlBody.append("<p>If you did not request this, please ignore this email.</p>");
                htmlBody.append("<br/><p>Regards,<br/>Thirdeye Team</p></div>");

                String subject;
                String fromEmail;

                if (mail.getMailType() == 1) {
                    subject = "Verify email - ThirdEye";
                    fromEmail = "thirdeye-verify-email@thirdeye3.com";
                } else {
                    subject = "Recover password - ThirdEye";
                    fromEmail = "recover-password@thirdeye3.com";
                }

                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");

                String jsonBody =
                        "{\n" +
                        "  \"sender\": {\n" +
                        "     \"name\": \"ThirdEye\",\n" +
                        "     \"email\": \"" + fromEmail + "\"\n" +
                        "  },\n" +
                        "  \"to\": [\n" +
                        "    { \"email\": \"" + mail.getUserName() + "\" }\n" +
                        "  ],\n" +
                        "  \"subject\": \"" + subject + "\",\n" +
                        "  \"htmlContent\": \"" + htmlBody.toString().replace("\"", "\\\"") + "\"\n" +
                        "}";

                RequestBody body = RequestBody.create(jsonBody, mediaType);

                Request request = new Request.Builder()
                        .url("https://api.brevo.com/v3/smtp/email")
                        .post(body)
                        .addHeader("accept", "application/json")
                        .addHeader("api-key", brevoApiKey)
                        .addHeader("content-type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                String res = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    mailRepository.markSend(mail.getId());
                    logger.info("OTP Mail sent successfully to: {}", mail.getUserName());
                } else {
                    logger.error("Failed sending email. Response: {}", res);
                }

            } catch (Exception e) {
                logger.error("FAILED to send mail to {}", mail.getUserName());
                logger.error("Reason: {}", e.getMessage());
            }

            mailRepository.decreaseTries(mail.getId());
        }
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
    	LocalDateTime now = timeManager.getLocalCurrentTime();
    	Long inactivMails = mailRepository.countExpiredOrFailed(now);
    	if(inactivMails == 0)
    	{
    		return;
    	}
        logger.info("🧹 Cleaning {} expired / used / failed OTP records...", inactivMails);
        mailRepository.deleteExpiredOrFailed(now);
        logger.info("✅ Cleanup completed.");
    }
}
