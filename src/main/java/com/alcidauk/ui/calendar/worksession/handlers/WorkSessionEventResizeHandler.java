package com.alcidauk.ui.calendar.worksession.handlers;

import com.alcidauk.ui.calendar.worksession.WorkSessionCalendarEventProvider;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicEventResizeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by alcidauk on 06/09/16.
 */
public class WorkSessionEventResizeHandler extends BasicEventResizeHandler {

    private static final Logger log = LoggerFactory.getLogger(WorkSessionEventResizeHandler.class);

    private Calendar calendarHandler;

    @Override
    public void eventResize(CalendarComponentEvents.EventResize event) {
        calendarHandler = event.getComponent();
        super.eventResize(event);
    }

    @Override
    protected void setDates(EditableCalendarEvent event, Date start, Date end) {
        WorkSessionCalendarEventBean calendarBean = (WorkSessionCalendarEventBean) event;

        super.setDates(event, start, end);
        ((WorkSessionCalendarEventProvider) calendarHandler.getEventProvider()).updateWorkSession(calendarBean);
    }

}
