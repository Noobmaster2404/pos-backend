package com.increff.commons.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.util.Objects;

public class TimeZoneUtil {
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    
    public static ZonedDateTime toUTC(ZonedDateTime dateTime) {
        if (Objects.isNull(dateTime)) return null;
        return dateTime.withZoneSameInstant(ZoneOffset.UTC);
    }
    
    public static ZonedDateTime toIST(ZonedDateTime dateTime) {
        if (Objects.isNull(dateTime)) return null;
        return dateTime.withZoneSameInstant(IST);
    }

    public static ZonedDateTime getCurrentUTCDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static ZonedDateTime getStartOfDay(ZonedDateTime dateTime) {
        // Convert to IST first, then get start of day, then convert back to UTC
        return toIST(dateTime).toLocalDate().atStartOfDay(IST).withZoneSameInstant(ZoneOffset.UTC);
    }

    public static ZonedDateTime getEndOfDay(ZonedDateTime dateTime) {
        // Convert to IST first, then get end of day, then convert back to UTC
        return toIST(dateTime).toLocalDate().atTime(23, 59, 59).atZone(IST).withZoneSameInstant(ZoneOffset.UTC);
    }

    public static ZonedDateTime getStartOfDay(LocalDate date) {
        if (Objects.isNull(date)) return null;
        // Use IST for start of day, then convert to UTC
        return date.atStartOfDay(IST).withZoneSameInstant(ZoneOffset.UTC);
    }

    public static ZonedDateTime getEndOfDay(LocalDate date) {
        if (Objects.isNull(date)) return null;
        // Use IST for end of day, then convert to UTC
        return date.atTime(23, 59, 59).atZone(IST).withZoneSameInstant(ZoneOffset.UTC);
    }
}