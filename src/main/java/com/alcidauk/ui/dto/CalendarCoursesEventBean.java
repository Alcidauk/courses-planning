package com.alcidauk.ui.dto;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class CalendarCoursesEventBean implements CalendarEvent {

    private CalendarCoursesEvent calendarCoursesEvent;

    public CalendarCoursesEventBean(CalendarCoursesEvent calendarCoursesEvent) {
        this.calendarCoursesEvent = calendarCoursesEvent;
    }

    @Override
    public Date getStart() {
        return Date.from(calendarCoursesEvent.getStartInstant());
    }

    @Override
    public Date getEnd() {
        return Date.from(calendarCoursesEvent.getEndInstant());
    }

    @Override
    public String getCaption() {
        return calendarCoursesEvent.getTitle() != null ?
                calendarCoursesEvent.getTitle() : calendarCoursesEvent.getType().getName();
    }

    @Override
    public String getDescription() {
        return calendarCoursesEvent.getDescription();
    }

    @Override
    public String getStyleName() {
        return calendarCoursesEvent.getType().getName();
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    public CalendarCoursesEvent getCalendarCoursesEvent() {
        return calendarCoursesEvent;
    }

    public boolean getDone(){
        return calendarCoursesEvent.isDone();
    }

    public void setDone(boolean done){
        calendarCoursesEvent.setDone(done);
    }

}
