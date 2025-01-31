package com.increff.commons.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.util.Objects;
public class TimeZoneUtil {
    public static ZonedDateTime toUTC(ZonedDateTime dateTime) {
        if (Objects.isNull(dateTime)) return null;
        return dateTime.withZoneSameInstant(ZoneOffset.UTC);
    }
    
    public static ZonedDateTime toIST(ZonedDateTime dateTime) {
        if (Objects.isNull(dateTime)) return null;
        return dateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
    }

    public static LocalDate getCurrentUTCDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    public static ZonedDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC);
    }

    public static ZonedDateTime getEndOfDay(LocalDate date) {
        return date.plusDays(1).atStartOfDay(ZoneOffset.UTC);
    }
}