package com.alcidauk.ui.dto;

import com.alcidauk.data.bean.DefaultSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class DefaultSessionCalendarBean implements CalendarEvent {

    private DefaultSession defaultSession;

    private Date startDate;

    public DefaultSessionCalendarBean(DefaultSession defaultSession, Date startDate) {
        this.defaultSession = defaultSession;
        this.startDate = startDate;
    }

    @Override
    public Date getStart() {
        Instant startInstant = startDate.toInstant();
        ZonedDateTime zonedDate = startInstant.atZone(ZoneId.systemDefault());
        zonedDate = zonedDate.with(WeekFields.of(UI.getCurrent().getLocale()).dayOfWeek(), defaultSession.getDayOfWeek());
        zonedDate = zonedDate.with(ChronoField.HOUR_OF_DAY, defaultSession.getStartHour());

        return Date.from(zonedDate.toInstant());
    }

    @Override
    public Date getEnd() {
        Instant startInstant = startDate.toInstant();
        ZonedDateTime zonedDate = startInstant.atZone(ZoneId.systemDefault());
        zonedDate = zonedDate.with(WeekFields.of(UI.getCurrent().getLocale()).dayOfWeek(), defaultSession.getDayOfWeek());
        zonedDate = zonedDate.with(ChronoField.HOUR_OF_DAY, defaultSession.getStartHour());
        zonedDate = zonedDate.plus(defaultSession.getDuration());

        return Date.from(zonedDate.toInstant());
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getStyleName() {
        return null;
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

}
