package com.alcidauk.ui.dto;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSession;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionCalendarEventBean implements CalendarEvent {

    private WorkSession workSession;

    public WorkSessionCalendarEventBean(WorkSession workSession) {
        this.workSession = workSession;
    }

    @Override
    public Date getStart() {
        return Date.from(workSession.getStartInstant());
    }

    @Override
    public Date getEnd() {
        return Date.from(workSession.getEndInstant());
    }

    @Override
    public String getCaption() {
        return workSession.getTitle() != null ?
                workSession.getTitle() : Messages.getWorkSessionTypeNameMessage(workSession.getType().getName());
    }

    @Override
    public String getDescription() {
        return workSession.getDescription();
    }

    @Override
    public String getStyleName() {
        return workSession.getType().getName();
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    public WorkSession getWorkSession() {
        return workSession;
    }

    public boolean getDone(){
        return workSession.isDone();
    }

    public void setDone(boolean done){
        workSession.setDone(done);
    }

}
