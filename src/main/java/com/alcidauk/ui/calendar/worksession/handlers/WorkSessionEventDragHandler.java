package com.alcidauk.ui.calendar.worksession.handlers;

import com.alcidauk.ui.calendar.worksession.WorkSessionCalendarEventProvider;
import com.alcidauk.ui.calendar.worksession.WorkSessionSettingsWindow;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;

/**
 * Created by alcidauk on 06/09/16.
 */
public class WorkSessionEventDragHandler implements CalendarComponentEvents.RangeSelectHandler {

    @Override
    public void rangeSelect(CalendarComponentEvents.RangeSelectEvent rangeSelectEvent) {
        Calendar calendarHandler = rangeSelectEvent.getComponent();

        WorkSessionCalendarEventBean calendarEvent = new WorkSessionCalendarEventBean();

        WorkSessionCalendarEventProvider eventProvider = (WorkSessionCalendarEventProvider) calendarHandler.getEventProvider();

        eventProvider.createSessionBean(calendarEvent);
        calendarEvent.setStart(rangeSelectEvent.getStart());
        calendarEvent.setEnd(rangeSelectEvent.getEnd());

        WorkSessionSettingsWindow workSessionSettingsWindow =
                new WorkSessionSettingsWindow(calendarEvent, eventProvider.getWorkSessionRepository(), eventProvider.getWorkSessionTypeRepository());

        workSessionSettingsWindow.init();

        UI.getCurrent().addWindow(workSessionSettingsWindow);
        workSessionSettingsWindow.center();
    }
}
