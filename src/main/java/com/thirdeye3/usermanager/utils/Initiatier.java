package com.thirdeye3.usermanager.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.thirdeye3.usermanager.services.BackupService;
import com.thirdeye3.usermanager.services.PropertyService;

//import com.thirdeye3.usermanager.services.PropertyService;

import jakarta.annotation.PostConstruct;

@Component
public class Initiatier {
	
    private static final Logger logger = LoggerFactory.getLogger(Initiatier.class);
	
	@Autowired
	private PropertyService propertyService;
	
	@Autowired
	private BackupService backupService;

    @Value("${thirdeye.priority}")
    private Integer priority;
    
    @Value("${thirdeye.jwt.secret}")
    private String secretKey;

    @Value("${thirdeye.jwt.token.starter}")
    private String starter;
	
	@PostConstruct
    public void init() throws Exception{
        logger.info("Initializing Initiatier...");
    	TimeUnit.SECONDS.sleep(priority * 3);
        logger.info("Starter is {} and length is {}", starter, starter.length());
        logger.info("Secret key is {} and length is {}", secretKey, secretKey.length());
        propertyService.fetchProperties();
        logger.info("Initiatier initialized.");
    }
	
	public void refreshMemory()
	{
		logger.info("Going to refersh memory...");
		backupService.exportAllToZip();
		logger.info("Memory refreshed.");
	}

}
