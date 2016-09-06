package com.alcidauk.ui.dto;

import com.alcidauk.data.bean.DefaultSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class DefaultSessionCalendarBean implements EditableCalendarEvent {

    private DefaultSession defaultSession;

    private Date startDate;
    private Date endDate;

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
    public void setEnd(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public void setStart(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    public DefaultSession getDefaultSession() {
        return defaultSession;
    }

    public void updateDefaultSession() {
        ZonedDateTime newStartDate = startDate.toInstant().atZone(ZoneId.systemDefault());
        defaultSession.setStartHour(newStartDate.getHour());
        defaultSession.setDuration(Duration.between(startDate.toInstant(), endDate.toInstant()));
    }

    /*
     * UNUSED
     */

    @Override
    public void setCaption(String s) {

    }

    @Override
    public void setDescription(String s) {

    }

    @Override
    public void setStyleName(String s) {

    }

    @Override
    public void setAllDay(boolean b) {

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

}
