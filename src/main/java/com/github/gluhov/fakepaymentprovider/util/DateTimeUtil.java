package com.github.gluhov.fakepaymentprovider.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@UtilityClass
public final class DateTimeUtil {
    public static LocalDateTime dayOrMin(Long localDate) {
        return localDate != null ? LocalDateTime.ofInstant(Instant.ofEpochSecond(localDate), TimeZone.getDefault().toZoneId()) : LocalDate.now().atStartOfDay();
    }

    public static LocalDateTime dayOrMax(Long localDate) {
        return localDate != null ? LocalDateTime.ofInstant(Instant.ofEpochSecond(localDate), TimeZone.getDefault().toZoneId()) : LocalDate.now().atTime(LocalTime.MAX);
    }
}