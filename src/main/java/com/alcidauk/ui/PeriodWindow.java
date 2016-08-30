package com.alcidauk.ui;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.data.repository.CalendarCoursesEventTypeRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.ui.calendar.CalendarDetailWindow;
import com.alcidauk.ui.dto.CalendarCoursesEventBean;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

  /*      calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            public void eventClick(CalendarComponentEvents.EventClick event) {
                CalendarDetailWindow calendarDetailWindow =
                        new CalendarDetailWindow(coursesEventRepository, (CalendarCoursesEventBean) event.getCalendarEvent());
                calendarDetailWindow.init();

                UI.getCurrent().addWindow(calendarDetailWindow);
                calendarDetailWindow.center();
            }
        });*/

        final BeanItemContainer<CalendarCoursesEventBean> container =
                new BeanItemContainer<>(CalendarCoursesEventBean.class);

        CalendarCoursesEventType calendarCoursesEventType = coursesEventTypeRepository.findByName("Disponible");
        List<CalendarCoursesEvent> coursesEvents = coursesEventRepository.findByType(calendarCoursesEventType);

        List<CalendarCoursesEventBean> coursesEventBean = coursesEvents.stream()
                .map(CalendarCoursesEventBean::new).collect(Collectors.toList());

        container.addAll(coursesEventBean);
        calendar.setContainerDataSource(container, "caption",
                "description", "start", "end", "styleName");

        calendar.setHeight(100, Unit.PERCENTAGE);
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
    }
}
