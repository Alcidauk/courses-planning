package com.alcidauk.ui.calendar;

import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by alcidauk on 06/09/16.
 */
public class DefaultSessionEventMoveHandler extends BasicEventMoveHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionEventMoveHandler.class);

    private Calendar calendarHandler;

    @Override
    public void eventMove(CalendarComponentEvents.MoveEvent event) {
        calendarHandler = event.getComponent();
        super.eventMove(event);
    }

    @Override
    protected void setDates(EditableCalendarEvent event, Date start, Date end) {
        DefaultSessionCalendarBean calendarBean = (DefaultSessionCalendarBean) event;

        if(getDayOfWeek(calendarBean.getStart()).equals(getDayOfWeek(start)) &&
                getDayOfWeek(calendarBean.getEnd()).equals(getDayOfWeek(end))) {
            super.setDates(event, start, end);
            ((DefaultSessionsEventProvider) calendarHandler.getEventProvider()).updateSessionBean(calendarBean);
        }
    }

    private DayOfWeek getDayOfWeek(Date start) {
        return ZonedDateTime.ofInstant(start.toInstant(), ZoneId.systemDefault()).getDayOfWeek();
    }

}
