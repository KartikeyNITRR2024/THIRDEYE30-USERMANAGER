package com.thirdeye3.usermanager.utils;

import java.sql.Timestamp;
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

    public Timestamp getCurrentTime() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZone));
        LocalDateTime localDateTime = currentTime.toLocalDateTime();
        return Timestamp.valueOf(localDateTime);
    }
}


