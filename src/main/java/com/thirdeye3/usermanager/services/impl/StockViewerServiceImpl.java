package com.thirdeye3.usermanager.services.impl;

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

        try {
            stockViewer.updateOrAddThresholdGroupDto(thresholdGroupDto);
            logger.info("Successfully updated thresholdGroupDto in StockViewer for groupid={}", thresholdGroupDto.getId());
        } catch (Exception e) {
            logger.error("Failed to update StockViewer for groupid={}. Reason: {}", thresholdGroupDto.getId(), e.getMessage(), e);
        }
	}
}
