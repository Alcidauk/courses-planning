package com.alcidauk.ui;

import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.data.repository.CalendarCoursesEventTypeRepository;
import com.alcidauk.ui.calendar.AvailableCalendarCoursesEventProvider;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Created by alcidauk on 29/08/16.
 */
public class PeriodWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(PeriodWindow.class);


    private Instant startInstant;
    private Instant endInstant;

    private CalendarCoursesEventRepository coursesEventRepository;
    private CalendarCoursesEventTypeRepository coursesEventTypeRepository;

    private VerticalLayout mainLayout;

    private Calendar calendar;

    public PeriodWindow(Instant startDate, Instant endDate, CalendarCoursesEventRepository coursesEventRepository,
                        CalendarCoursesEventTypeRepository coursesEventTypeRepository) {
        this.startInstant = startDate;
        this.endInstant = endDate;
        this.coursesEventRepository = coursesEventRepository;
        this.coursesEventTypeRepository = coursesEventTypeRepository;
    }

    public void init(){
        calendar = new Calendar();
        calendar.setLocale(Locale.FRENCH);
        calendar.setFirstVisibleHourOfDay(6);
        calendar.setLastVisibleHourOfDay(21);

        AvailableCalendarCoursesEventProvider calendarProvider = new AvailableCalendarCoursesEventProvider(coursesEventRepository, coursesEventTypeRepository);
        calendar.setEventProvider(calendarProvider);

        calendar.addStyleName("calendar");

        mainLayout = new VerticalLayout();

        Button validButton = new Button("Planning par défaut");

        HorizontalLayout buttonsLayout = new HorizontalLayout(validButton);
        buttonsLayout.setMargin(true);
        buttonsLayout.setWidth(100, Unit.PERCENTAGE);

        mainLayout.addComponent(calendar);
        mainLayout.addComponent(buttonsLayout);

        mainLayout.setMargin(true);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dates = String.format("du %s au %s", dateTimeFormatter.format(startInstant.atZone(ZoneId.systemDefault())),
                dateTimeFormatter.format(endInstant.atZone(ZoneId.systemDefault())));

        setCaption(String.format("Planning %s", dates));
        setContent(mainLayout);
    }
}
