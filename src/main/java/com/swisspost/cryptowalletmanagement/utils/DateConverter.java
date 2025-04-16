package com.swisspost.cryptowalletmanagement.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateConverter {
    public static long getStartEpoch(LocalDate date){
        LocalDateTime originalDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
        ZonedDateTime zonedDateTime = originalDateTime.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static long getEndEpoch(LocalDate date){
        LocalDateTime originalDateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0);
        LocalDateTime updatedDateTime = originalDateTime.plusHours(23).plusMinutes(59).plusSeconds(59);
        ZonedDateTime zonedDateTime = originalDateTime.atZone(ZoneId.of("UTC"));
        return zonedDateTime.toInstant().toEpochMilli();
    }
}
