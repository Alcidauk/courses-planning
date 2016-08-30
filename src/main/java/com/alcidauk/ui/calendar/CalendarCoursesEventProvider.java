package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.ui.dto.CalendarCoursesEventBean;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 30/08/16.
 */
public class CalendarCoursesEventProvider implements CalendarEventProvider {

    private CalendarCoursesEventRepository coursesEventRepository;

    private static final Logger log = LoggerFactory.getLogger(CalendarCoursesEventProvider.class);

    public CalendarCoursesEventProvider(CalendarCoursesEventRepository coursesEventRepository) {
        this.coursesEventRepository = coursesEventRepository;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        List<CalendarCoursesEvent> calendarCoursesEvents = coursesEventRepository.findBetweenStartInstantAndEndInstant(start.toInstant(), end.toInstant());

        log.info(calendarCoursesEvents.size() + "found !");
        return calendarCoursesEvents.stream().map(CalendarCoursesEventBean::new).collect(Collectors.toList());
    }
}
