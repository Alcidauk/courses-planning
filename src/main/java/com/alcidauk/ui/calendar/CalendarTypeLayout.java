package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.ui.CoursesUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alcidauk on 23/08/16.
 */
public class CalendarTypeLayout extends VerticalLayout implements CalendarCoursesEventTypeListener{

    private static final Logger log = LoggerFactory.getLogger(CalendarTypeLayout.class);


    private CalendarCoursesEventType calendarCoursesEventType;
    private CalendarCoursesEventRepository coursesEventRepository;

    private Label calendarTypeLabel;

    private Label doneEvents;
    private Label leftEvents;

    public CalendarTypeLayout(CalendarCoursesEventRepository calendarCoursesEventRepository, CalendarCoursesEventType calendarCoursesEventType) {
        this.calendarCoursesEventType = calendarCoursesEventType;
        this.coursesEventRepository = calendarCoursesEventRepository;
    }

    public void init(){
        calendarTypeLabel = new Label(StringUtils.capitalize(calendarCoursesEventType.getName()) + " : ");
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
        return "Session(s) restante(s) " + coursesEventRepository.findLeftByType(calendarCoursesEventType).size();
    }

    private String getDoneTextValue() {
        return "Session(s) réalisée(s) " + coursesEventRepository.findDoneByType(calendarCoursesEventType).size();
    }

    @Override
    public void update(CalendarCoursesEventTypeUpdatedEvent calendarCoursesEventTypeUpdatedEvent) {
        if(calendarCoursesEventTypeUpdatedEvent.getCalendarCoursesEventType().getId().equals(calendarCoursesEventType.getId())) {
            calendarCoursesEventType = calendarCoursesEventTypeUpdatedEvent.getCalendarCoursesEventType();
            updateLabels();
        }
    }

    private void updateLabels() {
       doneEvents.setValue(getDoneTextValue());
       leftEvents.setValue(getLeftTextValue());

        this.markAsDirty();
    }
}
