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

    public static ZonedDateTime getCurrentUTCDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static ZonedDateTime getStartOfDay(ZonedDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay(ZoneOffset.UTC);
    }

    public static ZonedDateTime getEndOfDay(ZonedDateTime dateTime) {
        return dateTime.toLocalDate().plusDays(1).atStartOfDay(ZoneOffset.UTC);
    }

    public static ZonedDateTime getStartOfDay(LocalDate date) {
        if (Objects.isNull(date)) return null;
        return date.atStartOfDay(ZoneOffset.UTC);
    }

    public static ZonedDateTime getEndOfDay(LocalDate date) {
        if (Objects.isNull(date)) return null;
        return date.plusDays(1).atStartOfDay(ZoneOffset.UTC);
    }
}