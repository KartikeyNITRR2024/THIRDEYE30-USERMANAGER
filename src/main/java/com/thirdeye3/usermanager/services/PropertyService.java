package com.thirdeye3.usermanager.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PropertyService {

    void fetchProperties();

    Long getMaximumNoOfUsers();

    Long getMaximumNoOfThresoldPerGroup();

    Long getMaximumNoOfHoldedStockPerUser();

    Long getMaximumNoOfGroupPerUser();

    Set<Long> getTimeGapListForThresoldInSeconds();

	Long getOtpExpiryTimeInMinutes();

	Long getMaximumNoOfTriesToSendOtp();
}
