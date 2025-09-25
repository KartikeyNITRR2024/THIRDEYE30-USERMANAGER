package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.MessengerPayload;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.externalcontollers.MessengerClient;
import com.thirdeye3.usermanager.services.MessengerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MessengerServiceImpl implements MessengerService {

    private static final Logger logger = LoggerFactory.getLogger(MessengerServiceImpl.class);

    @Autowired
    private MessengerClient messenger;
    
    @Async
    @Override
	public void updateMessenger(ThresholdGroupDto thresholdGroupDto) {
    	logger.info("Async call triggered: updating messenger for groupid={}",
    			thresholdGroupDto.getId());

        try {
        	messenger.updateOrAddMessenger(thresholdGroupDto);
            logger.info("Successfully updated messsenger for groupid={}", thresholdGroupDto.getId());
        } catch (Exception e) {
            logger.error("Failed to update messenger for groupid={}. Reason: {}", thresholdGroupDto.getId(), e.getMessage(), e);
        }
	}
}
