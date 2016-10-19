package com.alcidauk.ui.dto;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSession;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;

import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionCalendarEventBean implements EditableCalendarEvent {

    private WorkSession workSession;

    public WorkSessionCalendarEventBean(WorkSession workSession) {
        this.workSession = workSession;
    }

    @Override
    public Date getStart() {
        return Date.from(workSession.getStartInstant());
    }

    @Override
    public void setStart(Date startDate){
        workSession.setStartInstant(startDate.toInstant());
    }

    @Override
    public void setStyleName(String s) {
    }

    @Override
    public void setAllDay(boolean b) {
    }

    @Override
    public Date getEnd() {
        return Date.from(workSession.getEndInstant());
    }

    @Override
    public void setCaption(String caption) {
        workSession.setTitle(caption);
    }

    @Override
    public void setDescription(String description) {
        workSession.setDescription(description);
    }

    @Override
    public void setEnd(Date endDate){
        workSession.setEndInstant(endDate.toInstant());
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
