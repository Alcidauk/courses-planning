package com.alcidauk.ui.calendar;

import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by alcidauk on 06/09/16.
 */
public class CalendarUtils {

    private static final Logger log = LoggerFactory.getLogger(CalendarUtils.class);

    public static Instant getDateInWeek(Date randomDateInWeek, int dayOfWeek, int startHour) {
        ZonedDateTime zonedDate = getZonedDateTimeInWeek(randomDateInWeek, dayOfWeek, startHour);
        return zonedDate.toInstant();
    }

    public static Instant getDateInWeek(Date randomDateInWeek, int dayOfWeek, int startHour, Duration duration) {
        ZonedDateTime zonedDate = getZonedDateTimeInWeek(randomDateInWeek, dayOfWeek, startHour);
        zonedDate = zonedDate.plus(duration);

        return zonedDate.toInstant();
    }

    private static ZonedDateTime getZonedDateTimeInWeek(Date randomDateInWeek, int dayOfWeek, int startHour) {
        Instant startInstant = randomDateInWeek.toInstant();
        ZonedDateTime zonedDate = startInstant.atZone(ZoneId.systemDefault());
        zonedDate = zonedDate.with(WeekFields.of(UI.getCurrent().getLocale()).dayOfWeek(), dayOfWeek);
        zonedDate = zonedDate.with(ChronoField.HOUR_OF_DAY, startHour);
        return zonedDate;
    }

    public static LocalDateTime getLocalDateTimeFromInstant(Instant instant, int timezoneMillisOffset){
        ZoneId zoneIdFromTimezoneMillisOffset = getZoneIdFromTimezoneMillisOffset(timezoneMillisOffset);
        if(zoneIdFromTimezoneMillisOffset == null){
            return null;
        } else {
            return LocalDateTime.ofInstant(instant, zoneIdFromTimezoneMillisOffset);
        }
    }

    public static Instant getInstantFromLocalDateTime(LocalDateTime localDateTime, int timezoneMillisOffset){
        ZoneId zoneIdFromTimezoneMillisOffset = getZoneIdFromTimezoneMillisOffset(timezoneMillisOffset);
        if(zoneIdFromTimezoneMillisOffset == null){
            return null;
        } else {
            return ZonedDateTime.of(localDateTime, zoneIdFromTimezoneMillisOffset).toInstant();
        }
    }

    public static ZoneId getZoneIdFromTimezoneMillisOffset(int timezoneMillisOffset){
        log.info("Offset : " + timezoneMillisOffset);
        String[] availableIDs = TimeZone.getAvailableIDs(timezoneMillisOffset);
        if(availableIDs == null || availableIDs.length == 0){
            log.error(String.format("No timezone corresponding to offset %d", timezoneMillisOffset));
            return null;
        } else {
            log.info("Zone  : " + availableIDs[0]);
            return ZoneId.of(availableIDs[0]);
        }
    }
}
