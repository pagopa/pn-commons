package it.pagopa.pn.commons.utils;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public abstract class DateFormatUtils {
    public static final String yyyyMMddHHmmssSSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

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
}
