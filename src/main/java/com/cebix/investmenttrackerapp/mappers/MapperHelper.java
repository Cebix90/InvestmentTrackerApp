package com.cebix.investmenttrackerapp.mappers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class MapperHelper {
    public static LocalDate convertTimestampToLocalDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return instant.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
