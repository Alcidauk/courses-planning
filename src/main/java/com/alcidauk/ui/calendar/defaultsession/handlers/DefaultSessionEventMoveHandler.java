package com.alcidauk.ui.calendar.defaultsession.handlers;

import com.alcidauk.ui.calendar.defaultsession.DefaultSessionsEventProvider;
import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
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

        if(start.toInstant().isAfter(calendarHandler.getStartDate().toInstant().minus(1, ChronoUnit.MICROS)) &&
                end.toInstant().isBefore(calendarHandler.getEndDate().toInstant().plus(1, ChronoUnit.MICROS))) {
            super.setDates(event, start, end);
            ((DefaultSessionsEventProvider) calendarHandler.getEventProvider()).updateSessionBean(calendarBean);
        }
    }

}
