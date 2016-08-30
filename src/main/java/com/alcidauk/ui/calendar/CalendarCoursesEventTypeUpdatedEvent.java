package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.vaadin.ui.Component;

/**
 * Created by alcidauk on 24/08/16.
 */
public class CalendarCoursesEventTypeUpdatedEvent extends Component.Event{

    private CalendarCoursesEventType calendarCoursesEventType;

    public CalendarCoursesEventTypeUpdatedEvent(Component source, CalendarCoursesEventType calendarCoursesEventType) {
        super(source);
        this.calendarCoursesEventType = calendarCoursesEventType;
    }

    public CalendarCoursesEventType getCalendarCoursesEventType() {
        return calendarCoursesEventType;
    }
}
