package com.alcidauk.ui.dto;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.time.Duration;
import java.util.Date;

/**
 * Created by alcidauk on 23/08/16.
 */
public class PlanningPeriodEventTypeBean {

    private PlanningPeriodEventType planningPeriodEventType;

    public PlanningPeriodEventTypeBean(PlanningPeriodEventType planningPeriodEventType) {
        this.planningPeriodEventType = planningPeriodEventType;
    }

    public String getTypeName(){
        return planningPeriodEventType.getType() != null ?
                Messages.getWorkSessionTypeNameMessage(planningPeriodEventType.getType().getName()) : "";
    }

    public long getDurationHours(){
        return planningPeriodEventType.getPeriodDuration() != null ? planningPeriodEventType.getPeriodDuration().toHours() : 0;
    }

    public void setDurationHours(long hours){
        planningPeriodEventType.setPeriodDuration(Duration.ofHours(hours));
    }

    public PlanningPeriodEventType getPlanningPeriodEventType() {
        return planningPeriodEventType;
    }
}
