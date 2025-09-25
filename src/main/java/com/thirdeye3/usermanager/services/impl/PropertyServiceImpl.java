package com.thirdeye3.usermanager.services.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.exceptions.PropertyFetchException;
import com.thirdeye3.usermanager.externalcontollers.PropertyManagerClient;
import com.thirdeye3.usermanager.services.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {
    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);
    
    @Autowired
    private PropertyManagerClient propertyManager;

    private Map<String, Object> properties = null;
    private Long maximumNoOfUsers = null;
    private Long maximumNoOfThresoldPerGroup = null;
    private Long maximumNoOfHoldedStockPerUser = null;
    private Long maximumNoOfGroupPerUser = null;
    private Set<Long> timeGapListForThresoldInSeconds = null;

    @Override
    public void fetchProperties() {
        Response<Map<String, Object>> response = propertyManager.getProperties();
        if (response.isSuccess()) {
            properties = response.getResponse();
            maximumNoOfUsers = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_USERS", 10L)).longValue();
            maximumNoOfThresoldPerGroup = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_THRESOLD_PER_GROUP", 10L)).longValue();
            maximumNoOfHoldedStockPerUser = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_HOLDED_STOCK_PER_USER", 10L)).longValue();
            maximumNoOfGroupPerUser = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_THRESOLD_GROUP_PER_USER", 10L)).longValue();
            String timeGapListForThresoldInSecondsString = (String) properties.getOrDefault("TIME_GAP_LIST_FOR_THRESOLD_IN_SECONDS", "60, 120, 180");
            Set<Long> timeGapSetForThresholdInSeconds = Arrays.stream(timeGapListForThresoldInSecondsString.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            logger.info("Request {}, maximumNoOfUsers {}, maximumNoOfThresoldPerGroup {} , maximumNoOfHoldedStockPerUser {}, maximumNoOfGroupPerUser {}, timeGapListForThresoldInSeconds {}",
                    properties, maximumNoOfUsers, maximumNoOfThresoldPerGroup, maximumNoOfHoldedStockPerUser, maximumNoOfGroupPerUser, timeGapListForThresoldInSeconds);
        } else {
            properties = new HashMap<>();
            logger.error("Failed to fetch properties");
            throw new PropertyFetchException("Unable to fetch properties from Property Manager");
        }
    }

    @Override
    public Long getMaximumNoOfUsers() {
    	if(maximumNoOfUsers == null)
    	{
    		fetchProperties();
    	}
        return maximumNoOfUsers;
    }

    @Override
    public Long getMaximumNoOfThresoldPerGroup() {
    	if(maximumNoOfThresoldPerGroup == null)
    	{
    		fetchProperties();
    	}
        return maximumNoOfThresoldPerGroup;
    }

    @Override
    public Long getMaximumNoOfHoldedStockPerUser() {
    	if(maximumNoOfHoldedStockPerUser == null)
    	{
    		fetchProperties();
    	}
        return maximumNoOfHoldedStockPerUser;
    }

    @Override
    public Long getMaximumNoOfGroupPerUser() {
    	if(maximumNoOfGroupPerUser == null)
    	{
    		fetchProperties();
    	}
        return maximumNoOfGroupPerUser;
    }
    
    @Override
    public Set<Long> getTimeGapListForThresoldInSeconds() {
    	if(timeGapListForThresoldInSeconds == null)
    	{
    		fetchProperties();
    	}
        return timeGapListForThresoldInSeconds;
    }
}
