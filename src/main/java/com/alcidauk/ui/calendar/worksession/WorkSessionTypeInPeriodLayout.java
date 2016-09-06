package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.ui.CoursesUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionTypeInPeriodLayout extends VerticalLayout implements WorkSessionTypeListener {

    private static final Logger log = LoggerFactory.getLogger(WorkSessionTypeInPeriodLayout.class);


    private WorkSessionType workSessionType;
    private WorkSessionRepository workSessionRepository;

    private Label calendarTypeLabel;

    private Label doneEvents;
    private Label leftEvents;

    public WorkSessionTypeInPeriodLayout(WorkSessionRepository workSessionRepository, WorkSessionType workSessionType) {
        this.workSessionType = workSessionType;
        this.workSessionRepository = workSessionRepository;
    }

    public void init(){
        calendarTypeLabel = new Label(StringUtils.capitalize(workSessionType.getName()) + " : ");
        calendarTypeLabel.addStyleName("bold-font");

        doneEvents = new Label(getDoneTextValue());
        leftEvents = new Label(getLeftTextValue());
        doneEvents.setImmediate(true);
        leftEvents.setImmediate(true);

        this.addComponent(calendarTypeLabel);
        this.addComponent(doneEvents);
        this.addComponent(leftEvents);

        this.setMargin(true);
        this.setImmediate(true);

        ((CoursesUI) UI.getCurrent()).addCalendarCoursesEventTypeListeners(this);
    }

    private String getLeftTextValue() {
        return "Session(s) restante(s) " + workSessionRepository.findLeftByType(workSessionType).size();
    }

    private String getDoneTextValue() {
        return "Session(s) réalisée(s) " + workSessionRepository.findDoneByType(workSessionType).size();
    }

    @Override
    public void update(WorkSessionTypeUpdatedEvent workSessionTypeUpdatedEvent) {
        if(workSessionTypeUpdatedEvent.getWorkSessionType().getId().equals(workSessionType.getId())) {
            workSessionType = workSessionTypeUpdatedEvent.getWorkSessionType();
            updateLabels();
        }
    }

    private void updateLabels() {
       doneEvents.setValue(getDoneTextValue());
       leftEvents.setValue(getLeftTextValue());

        this.markAsDirty();
    }
}
