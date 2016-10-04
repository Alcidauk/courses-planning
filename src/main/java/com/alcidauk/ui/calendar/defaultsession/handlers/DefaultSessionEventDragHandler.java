package com.alcidauk.ui.calendar.defaultsession.handlers;

import com.alcidauk.ui.calendar.defaultsession.DefaultSessionsEventProvider;
import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;

/**
 * Created by alcidauk on 06/09/16.
 */
public class DefaultSessionEventDragHandler implements CalendarComponentEvents.RangeSelectHandler {

    @Override
    public void rangeSelect(CalendarComponentEvents.RangeSelectEvent rangeSelectEvent) {
        Calendar calendarHandler = rangeSelectEvent.getComponent();

        DefaultSessionCalendarBean calendarEvent = new DefaultSessionCalendarBean();
        calendarEvent.setStart(rangeSelectEvent.getStart());
        calendarEvent.setEnd(rangeSelectEvent.getEnd());

        ((DefaultSessionsEventProvider) calendarHandler.getEventProvider()).createSessionBean(calendarEvent);

        calendarHandler.markAsDirty();
    }
}
