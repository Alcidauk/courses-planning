package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.PlanningPeriod;
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
public class WorkSessionTypeInPeriodLayout extends VerticalLayout implements WorkSessionTypeListener, ShownPlanningPeriodListener {

    private WorkSessionType workSessionType;
    private WorkSessionRepository workSessionRepository;

    private PlanningPeriod planningPeriod;

    private Label calendarTypeLabel;

    private Label hoursEvents;

    public WorkSessionTypeInPeriodLayout(WorkSessionRepository workSessionRepository, WorkSessionType workSessionType) {
        this.workSessionType = workSessionType;
        this.workSessionRepository = workSessionRepository;
    }

    public void init(){
        calendarTypeLabel = new Label(Messages.getWorkSessionTypeNameMessage(workSessionType.getName()) + " : ");
        calendarTypeLabel.addStyleName("bold-font");

        hoursEvents = new Label();
        hoursEvents.setImmediate(true);

        this.addComponent(calendarTypeLabel);
        this.addComponent(hoursEvents);

        this.setMargin(true);
        this.setImmediate(true);

        ((CoursesUI) UI.getCurrent()).addCalendarCoursesEventTypeListeners(this);
        ((CoursesUI) UI.getCurrent()).addShownPlanningPeriodListeners(this);
    }

    private String getHoursEventValue() {
        return String.format("%s / %s / %s : %d / %d / %d",
                Messages.getMessage("com.alcidauk.courses.planning.work.session.types.in.period.done"),
                Messages.getMessage("com.alcidauk.courses.planning.work.session.types.in.period.remaining"),
                Messages.getMessage("com.alcidauk.courses.planning.work.session.types.in.period.planned"),
                getLeftHoursValue(),
                getDoneHoursValue(),
                0);
    }

    private long getLeftHoursValue() {
        List<WorkSession> workSessions = workSessionRepository.findLeftBetweenStartInstantAndEndInstant(planningPeriod.getStartInstant(),
                planningPeriod.getEndInstant(), workSessionType);
        return countHoursInSessionList(workSessions);
    }

    private long getDoneHoursValue() {
        List<WorkSession> workSessions = workSessionRepository.findDoneBetweenStartInstantAndEndInstant(planningPeriod.getStartInstant(),
                planningPeriod.getEndInstant(), workSessionType);
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

    @Override
    public void update(ShowPlanningPeriodChangedEvent showPlanningPeriodChangedEvent) {
        planningPeriod =  showPlanningPeriodChangedEvent.getPlanningPeriod();
        updateLabels();
    }
}
