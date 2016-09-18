package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.repository.*;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.VaadinSessionScope;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * Created by alcidauk on 07/09/16.
 */
@SpringComponent
@VaadinSessionScope
public class WorkSessionCalendar extends Calendar {

    @Autowired
    private WorkSessionRepository workSessionRepository;

    @Autowired
    private WorkSessionTypeRepository workSessionTypeRepository;

    @Autowired
    private PlanningPeriodRepository planningPeriodRepository;

    @Autowired
    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;

    @Autowired
    private UserRepository userRepository;

    public void init() {
        setLocale(Locale.FRENCH);
        setFirstVisibleHourOfDay(6);
        setLastVisibleHourOfDay(21);

        setHandler(new CalendarComponentEvents.EventClickHandler() {
            public void eventClick(CalendarComponentEvents.EventClick event) {
                WorkSessionSettingsWindow workSessionSettingsWindow =
                        new WorkSessionSettingsWindow(workSessionRepository, (WorkSessionCalendarEventBean) event.getCalendarEvent());
                workSessionSettingsWindow.init();

                UI.getCurrent().addWindow(workSessionSettingsWindow);
                workSessionSettingsWindow.center();
            }
        });

        setEventProvider(new WorkSessionCalendarEventProvider(workSessionRepository, planningPeriodRepository,
                defaultUnavailabilitySessionRepository, workSessionTypeRepository, userRepository, this));

        setHeight(100, Unit.PERCENTAGE);
        addStyleName("calendar");
    }
}
