package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.data.repository.CalendarCoursesEventTypeRepository;
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
public class AvailableCalendarCoursesEventProvider implements CalendarEventProvider {

    private CalendarCoursesEventRepository coursesEventRepository;
    private CalendarCoursesEventTypeRepository coursesEventTypeRepository;

    private static final Logger log = LoggerFactory.getLogger(AvailableCalendarCoursesEventProvider.class);

    public AvailableCalendarCoursesEventProvider(CalendarCoursesEventRepository coursesEventRepository, CalendarCoursesEventTypeRepository coursesEventTypeRepository) {
        this.coursesEventRepository = coursesEventRepository;
        this.coursesEventTypeRepository = coursesEventTypeRepository;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        CalendarCoursesEventType calendarCoursesEventType = coursesEventTypeRepository.findByName("Disponible");

        List<CalendarCoursesEvent> calendarCoursesEvents =
                coursesEventRepository.findBetweenStartInstantAndEndInstantAndType(start.toInstant(), end.toInstant(), calendarCoursesEventType);

        return calendarCoursesEvents.stream().map(CalendarCoursesEventBean::new).collect(Collectors.toList());
    }
}
