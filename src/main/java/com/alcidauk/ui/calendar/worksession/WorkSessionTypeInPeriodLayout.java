package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.ui.CoursesUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.List;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionTypeInPeriodLayout extends VerticalLayout implements WorkSessionTypeListener {

    private WorkSessionType workSessionType;
    private WorkSessionRepository workSessionRepository;

    private Label calendarTypeLabel;

    private Label hoursEvents;

    public WorkSessionTypeInPeriodLayout(WorkSessionRepository workSessionRepository, WorkSessionType workSessionType) {
        this.workSessionType = workSessionType;
        this.workSessionRepository = workSessionRepository;
    }

    public void init(){
        calendarTypeLabel = new Label(StringUtils.capitalize(workSessionType.getName()) + " : ");
        calendarTypeLabel.addStyleName("bold-font");

        hoursEvents = new Label(getHoursEventValue());
        hoursEvents.setImmediate(true);

        this.addComponent(calendarTypeLabel);
        this.addComponent(hoursEvents);

        this.setMargin(true);
        this.setImmediate(true);

        ((CoursesUI) UI.getCurrent()).addCalendarCoursesEventTypeListeners(this);
    }

    private String getHoursEventValue() {
        return String.format("Effectuées / Restantes / Plannifiées : %d / %d / %d", getLeftHoursValue(), getDoneHoursValue(), 0);
    }

    private long getLeftHoursValue() {
        List<WorkSession> workSessions = workSessionRepository.findLeftByType(workSessionType);
        return countHoursInSessionList(workSessions);
    }

    private long getDoneHoursValue() {
        List<WorkSession> workSessions = workSessionRepository.findDoneByType(workSessionType);
        return countHoursInSessionList(workSessions);
    }

    private long countHoursInSessionList(List<WorkSession> workSessions) {
        long hours = 0;

        for(WorkSession workSession : workSessions){
            hours += Duration.between(workSession.getStartInstant(), workSession.getEndInstant()).toHours();
        }
        return hours;
    }

    @Override
    public void update(WorkSessionTypeUpdatedEvent workSessionTypeUpdatedEvent) {
        if(workSessionTypeUpdatedEvent.getWorkSessionType().getId().equals(workSessionType.getId())) {
            workSessionType = workSessionTypeUpdatedEvent.getWorkSessionType();
            updateLabels();
        }
    }

    private void updateLabels() {
        hoursEvents.setValue(getHoursEventValue());
        this.markAsDirty();
    }
}
