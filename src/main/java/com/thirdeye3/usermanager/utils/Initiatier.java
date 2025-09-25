package com.thirdeye3.usermanager.utils;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.thirdeye3.usermanager.services.PropertyService;

//import com.thirdeye3.usermanager.services.PropertyService;

import jakarta.annotation.PostConstruct;

@Component
public class Initiatier {
	
    private static final Logger logger = LoggerFactory.getLogger(Initiatier.class);
	
	@Autowired
	PropertyService propertyService;

    @Value("${thirdeye.priority}")
    private Integer priority;
	
	@PostConstruct
    public void init() throws Exception{
        logger.info("Initializing Initiatier...");
    	TimeUnit.SECONDS.sleep(priority * 3);
        propertyService.fetchProperties();
        logger.info("Initiatier initialized.");
    }
	
	public void refreshMemory()
	{
		logger.info("Going to refersh memory...");
		logger.info("Memory refreshed.");
	}

}
