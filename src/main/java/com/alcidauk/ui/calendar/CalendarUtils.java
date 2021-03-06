package com.alcidauk.ui.calendar;

import com.vaadin.ui.UI;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * Created by alcidauk on 06/09/16.
 */
public class CalendarUtils {

    public static boolean isAfterOrEqual(Instant instant, Instant compared) {
        return instant.isAfter(compared) || instant.equals(compared);
    }

    public static boolean isBeforeOrEqual(Instant instant, Instant compared) {
        return instant.isAfter(compared) || instant.equals(compared);
    }

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

    public static LocalDateTime getLocalDateTimeFromInstant(Instant instant){
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
