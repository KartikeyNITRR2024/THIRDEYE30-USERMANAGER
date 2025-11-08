package com.thirdeye3.usermanager.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TimeManager {
    
    @Value("${thirdeye.timezone}")
    private String timeZone;
    
    public LocalDateTime getLocalCurrentTime() {
    	ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZone));
    	return currentTime.toLocalDateTime();
    }

    public Timestamp getCurrentTime() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZone));
        LocalDateTime localDateTime = currentTime.toLocalDateTime();
        return Timestamp.valueOf(localDateTime);
    }
    
    public String getCurrentTimeString() {
    	Timestamp currentTimestamp = getCurrentTime();
        return new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(currentTimestamp);
    }
    
    
}


