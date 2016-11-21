package com.alcidauk.ui.dto;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;

import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionCalendarEventBean implements EditableCalendarEvent {

    private WorkSession workSession;

    public WorkSessionCalendarEventBean() {
        workSession = new WorkSession();
    }

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
        if(workSession.getType() != null) {
            String workSessionType = Messages.getWorkSessionTypeNameMessage(workSession.getType().getName());

            return workSession.getTitle() != null ?
                    String.format("%s : %s", workSessionType, workSession.getTitle()) : workSessionType;
        }
        return null;
    }

    @Override
    public String getDescription() {
        return workSession.getDescription();
    }

    @Override
    public String getStyleName() {
        return workSession.getType() != null ? workSession.getType().getName() : null;
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

    public WorkSessionType getType(){
        return workSession.getType();
    }

    public void setType(WorkSessionType type){
        workSession.setType(type);
    }

    public void createWorkSession() {
        if(workSession == null){
            workSession = new WorkSession();
        }
    }
}
