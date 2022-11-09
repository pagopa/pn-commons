package it.pagopa.pn.commons.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatUtilsTest {


    @Test
    void formatInstantToString() {

        String res = DateFormatUtils.formatInstantToString(Instant.EPOCH, "yyyy-MM-dd");
        assertEquals("1970-01-01", res);
    }

    @Test
    void getInstantFromString() {
        Instant instant = DateFormatUtils.getInstantFromString( "1970-01-01T10:00:00", "yyyy-MM-dd'T'HH:mm:ss");
        assertEquals(36000, instant.getEpochSecond());
    }

    @Test
    void formatDate() {
        String res = DateFormatUtils.formatDate(Instant.EPOCH);
        assertEquals("1970-01-01", res);
    }

    @Test
    void formatDateNull() {
        assertNull(DateFormatUtils.formatDate(null));
    }

    @Test
    void setSpecificTimeToDate() {
        ZonedDateTime d = ZonedDateTime.ofInstant(Instant.ofEpochMilli(36000L), DateFormatUtils.utcZoneId);

        d = DateFormatUtils.setSpecificTimeToDate(d, 11, 12, 13, 14);
        assertEquals(11, d.getHour());
        assertEquals(12, d.getMinute());
        assertEquals(13, d.getSecond());
        assertEquals(14, d.getNano());
    }

    @Test
    void formatTime() {
        ZonedDateTime d = ZonedDateTime.ofInstant(Instant.ofEpochMilli(36000000L), DateFormatUtils.utcZoneId);
        String res = DateFormatUtils.formatTime(d);
        assertEquals("1970-01-01T11:00:00+01:00", res);
    }

    @Test
    void parseDate() {
        ZonedDateTime d = DateFormatUtils.parseDate("1970-01-01");
        assertEquals(1970, d.getYear());
    }

    @Test
    void atStartOfDay() {
        ZonedDateTime d = ZonedDateTime.ofInstant(Instant.ofEpochMilli(36000L), DateFormatUtils.utcZoneId);

        d = DateFormatUtils.setSpecificTimeToDate(d, 11, 12, 13, 14);
        ZonedDateTime dres = DateFormatUtils.atStartOfDay(d.toInstant());
        assertEquals(0, dres.getHour());
        assertEquals(d.getDayOfYear(), dres.getDayOfYear());
    }

    @Test
    void parseTime() {
        ZonedDateTime d = DateFormatUtils.parseTime("1970-01-01T11:00:00+01:00");
        assertEquals(11, d.getHour());
    }

    @Test
    void getEndOfTheDay() {

        Instant now = Instant.now();
        Instant dres = DateFormatUtils.getEndOfTheDay();

        ZonedDateTime z = ZonedDateTime.ofInstant(now, DateFormatUtils.utcZoneId);
        ZonedDateTime zres = ZonedDateTime.ofInstant(dres, DateFormatUtils.utcZoneId);

        assertEquals(23, zres.getHour());
        assertEquals(59, zres.getMinute());
        assertEquals(z.getDayOfYear(), zres.getDayOfYear());
    }

    @Test
    void parseInstantToZonedDateTime() {
        ZonedDateTime z = DateFormatUtils.parseInstantToZonedDateTime(Instant.EPOCH);
        assertEquals(1970, z.getYear());
    }
}