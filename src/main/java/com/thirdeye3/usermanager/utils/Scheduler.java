package com.thirdeye3.usermanager.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.externalcontollers.SelfClient;
import com.thirdeye3.usermanager.services.MailService;
@Component
public class Scheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);
	
    @Autowired
    SelfClient selfClient;
    
    @Autowired
    TimeManager timeManager;
    
    @Autowired
    Initiatier initiatier;
    
    @Autowired
    private MailService mailService;
    
    @Value("${thirdeye.uniqueId}")
    private Integer uniqueId;

    @Value("${thirdeye.uniqueCode}")
    private String uniqueCode;
	
	@Scheduled(fixedRate = 30000)
    public void checkStatusTask() {
        Response<String> response = selfClient.statusChecker(uniqueId, uniqueCode);
        logger.info("Status check response is {}", response.getResponse());
    }
	
	@Scheduled(fixedRate = 5000)
	public void sendMails() {
		mailService.sendOtp();
    }
	
	@Scheduled(fixedRate = 120000)
	public void removeMails() {
		mailService.deleteMails();
    }
	
    @Scheduled(cron = "${thirdeye.scheduler.cronToRefreshData}", zone = "${thirdeye.timezone}")
    public void runToRefreshdata() {
        try { 
            initiatier.refreshMemory();
            logger.info("🔄 Data refreshed at {}", timeManager.getCurrentTime());
        } catch (Exception e) {
            logger.error("❌ Failed to refresh data at {}: {}", timeManager.getCurrentTime(), e.getMessage());
        }
    }

}


