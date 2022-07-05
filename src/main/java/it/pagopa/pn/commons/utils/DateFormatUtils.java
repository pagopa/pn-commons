package it.pagopa.pn.commons.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class DateFormatUtils {
    private DateFormatUtils() {}
    
    public static final ZoneId italianZoneId =  ZoneId.of("Europe/Rome");

    @NotNull
    public static String formatInstantToString(Instant dateToFormat, String pattern) {
        return DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault())
                .format(dateToFormat);
    }

    @NotNull
    public static Instant getInstantFromString(String dateToFormat, String pattern) {
        return Instant.from( DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC") ).parse(dateToFormat));
    }

    public static String formatDate(Instant instant)
    {
        if (instant == null)
            return null;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        return LocalDate.ofInstant(instant, italianZoneId).format(formatter);
    }

    public static String formatTime(ZonedDateTime datetime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return datetime.format(formatter.withZone(italianZoneId));
    }

    public static ZonedDateTime parseDate(String date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        LocalDate locDate = LocalDate.parse(date, formatter);

        return locDate.atStartOfDay(italianZoneId);
    }

    public static ZonedDateTime atStartOfDay(Instant instant)
    {
        LocalDate locDate = LocalDate.ofInstant(instant, italianZoneId);
        return locDate.atStartOfDay(italianZoneId);
    }

    public static ZonedDateTime parseTime(String date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        return formatter.parse(date, ZonedDateTime::from);
    }

    public static ZonedDateTime parseInstantToZonedDateTime(Instant date)
    {
        return date.atZone(italianZoneId);
    }
}
