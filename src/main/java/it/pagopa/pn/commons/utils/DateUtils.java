package it.pagopa.pn.commons.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class DateUtils {
    public static final String yyyyMMddHHmmssSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @NotNull
    public static String formatInstantToString(Instant dateToFormat, String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(dateToFormat);
    }
    
    public static Date convertInstantToDate(Instant dateToConvert){
        return dateToConvert != null ? Date.from(dateToConvert) : null;
    }

    public static Instant convertDateToInstant(Date dateToConvert){
        return dateToConvert != null ? dateToConvert.toInstant() : null;
    }
}
