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

import jakarta.persistence.Column;

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
    private Long otpExpiryTimeInMinutes = null;
    private Long maximumNoOfTriesToSendOtp = null;
    private Integer maximumNoOfStocksPerGroup = null;
    private Boolean selectAllStocks = null;
    private Boolean isZeroAllowed = null;

    @Override
    public void fetchProperties() {
        Response<Map<String, Object>> response = propertyManager.getProperties();
        if (response.isSuccess()) {
            properties = response.getResponse();
            maximumNoOfUsers = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_USERS", 10L)).longValue();
            maximumNoOfThresoldPerGroup = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_THRESOLD_PER_GROUP", 10L)).longValue();
            maximumNoOfHoldedStockPerUser = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_HOLDED_STOCK_PER_USER", 10L)).longValue();
            maximumNoOfGroupPerUser = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_THRESOLD_GROUP_PER_USER", 10L)).longValue();
            String timeGapListForThresoldInSecondsString = properties.getOrDefault("TIME_GAP_LIST_FOR_THRESOLD_IN_SECONDS", "60, 120, 180").toString();
            if(timeGapListForThresoldInSecondsString != null && timeGapListForThresoldInSecondsString.length()>0)
            {
                timeGapListForThresoldInSeconds = Arrays.stream(timeGapListForThresoldInSecondsString.split(","))
                        .map(String::trim)
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
            }
            otpExpiryTimeInMinutes = ((Number) properties.getOrDefault("OTP_EXPIRY_TIME_IN_MINUTES", 5L)).longValue();
            maximumNoOfTriesToSendOtp = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_TRIES_TO_SEND_OTP", 5L)).longValue();
            maximumNoOfStocksPerGroup = ((Number) properties.getOrDefault("MAXIMUM_NO_OF_STOCK_PER_GROUP", 10)).intValue();
            selectAllStocks = (((Number) properties.getOrDefault("SELECT_ALL_STOCKS", 0)).intValue() == 1?true:false);
            isZeroAllowed = (((Number) properties.getOrDefault("IS_ZERO_ALLOWED", 0)).intValue() == 1?true:false);
            logger.info("Request {}, maximumNoOfUsers {}, maximumNoOfThresoldPerGroup {} , maximumNoOfHoldedStockPerUser {}, maximumNoOfGroupPerUser {}, timeGapListForThresoldInSeconds {}, otpExpiryTimeInMinutes {}, maximumNoOfTriesToSendOtp {}, maximumNoOfStocksPerGroup {}, selectAllStocks {}, isZeroAllowed {}",
                    properties, maximumNoOfUsers, maximumNoOfThresoldPerGroup, maximumNoOfHoldedStockPerUser, maximumNoOfGroupPerUser, timeGapListForThresoldInSeconds, otpExpiryTimeInMinutes, maximumNoOfTriesToSendOtp, maximumNoOfStocksPerGroup, selectAllStocks, isZeroAllowed);
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
    
    @Override
    public Long getOtpExpiryTimeInMinutes() {
    	if(otpExpiryTimeInMinutes == null)
    	{
    		fetchProperties();
    	}
        return otpExpiryTimeInMinutes;
    }
    
    @Override
    public Long getMaximumNoOfTriesToSendOtp() {
    	if(maximumNoOfTriesToSendOtp == null)
    	{
    		fetchProperties();
    	}
        return maximumNoOfTriesToSendOtp;
    }

    @Override
	public Integer getMaximumNoOfStocksPerGroup() {
		if(maximumNoOfStocksPerGroup == null)
    	{
    		fetchProperties();
    	}
		return maximumNoOfStocksPerGroup;
	}

    @Override
	public Boolean getSelectAllStocks() {
		if(selectAllStocks == null)
    	{
    		fetchProperties();
    	}
		return selectAllStocks;
	}

    @Override
	public Boolean getIsZeroAllowed() {
		if(isZeroAllowed == null)
    	{
    		fetchProperties();
    	}
		return isZeroAllowed;
	}
}
