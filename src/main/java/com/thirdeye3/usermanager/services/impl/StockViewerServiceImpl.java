package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdDto;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.externalcontollers.StockViewerClient;
import com.thirdeye3.usermanager.services.StockViewerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockViewerServiceImpl implements StockViewerService {

    private static final Logger logger = LoggerFactory.getLogger(StockViewerServiceImpl.class);

    @Autowired
    private StockViewerClient stockViewer;

    @Async
    @Override
	public void updateThresholdGroup(ThresholdGroupDto thresholdGroupDto) {
    	logger.info("Async call triggered: updating thresholdGroupDto for groupid={}",
    			thresholdGroupDto.getId());

    	Response<Boolean> finalResult = stockViewer.updateOrAddThresholdGroupDto(thresholdGroupDto); 
        if (finalResult.isSuccess()) {
            logger.info("Successfully broadcasted update to all StockViewer instances.");
        } else {
            logger.warn("Broadcast completed with issues: {}", finalResult.getErrorMessage());
        }
	}
}
