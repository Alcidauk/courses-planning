package com.alcidauk.ui.dto;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.ui.calendar.CalendarUtils;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class DefaultSessionCalendarBean implements EditableCalendarEvent {

    private DefaultUnavailabilitySession defaultUnavailabilitySession;

    private Date startDate;
    private Date endDate;

    public DefaultSessionCalendarBean(DefaultUnavailabilitySession defaultUnavailabilitySession, Date startDate) {
        this.defaultUnavailabilitySession = defaultUnavailabilitySession;
        this.startDate = startDate;
    }

    @Override
    public Date getStart() {
        return Date.from(CalendarUtils.getDateInWeek(startDate, defaultUnavailabilitySession.getDayOfWeek(), defaultUnavailabilitySession.getStartHour()));
    }

    @Override
    public Date getEnd() {
        return Date.from(CalendarUtils.getDateInWeek(startDate, defaultUnavailabilitySession.getDayOfWeek(),
                defaultUnavailabilitySession.getStartHour(), defaultUnavailabilitySession.getDuration()));
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

    public DefaultUnavailabilitySession getDefaultUnavailabilitySession() {
        return defaultUnavailabilitySession;
    }

    public void updateDefaultSession() {
        ZonedDateTime newStartDate = startDate.toInstant().atZone(ZoneId.systemDefault());

        defaultUnavailabilitySession.setStartHour(newStartDate.getHour());
        defaultUnavailabilitySession.setDayOfWeek(newStartDate.getDayOfWeek().getValue());
        defaultUnavailabilitySession.setDuration(Duration.between(startDate.toInstant(), endDate.toInstant()));
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
