package com.ezekielwong.ms.docs.utils;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for manipulating dates and time
 */
@Component
public class DateTimeUtils {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String getLocalDateTime(LocalDateTime localDateTime) {
        return dtf.format(localDateTime);
    }

    public String getTimeTaken(LocalDateTime start, LocalDateTime end) {
        return String.valueOf(Duration.between(start, end).toSeconds());
    }
}
