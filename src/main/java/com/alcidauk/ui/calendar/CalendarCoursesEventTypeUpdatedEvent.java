package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.WorkSessionType;
import com.vaadin.ui.Component;

/**
 * Created by alcidauk on 24/08/16.
 */
public class CalendarCoursesEventTypeUpdatedEvent extends Component.Event{

    private WorkSessionType workSessionType;

    public CalendarCoursesEventTypeUpdatedEvent(Component source, WorkSessionType workSessionType) {
        super(source);
        this.workSessionType = workSessionType;
    }

    public WorkSessionType getWorkSessionType() {
        return workSessionType;
    }
}
