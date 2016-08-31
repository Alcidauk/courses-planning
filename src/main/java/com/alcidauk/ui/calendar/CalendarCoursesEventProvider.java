package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.repository.WorkSessionRepository;
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

    private WorkSessionRepository workSessionRepository;

    private static final Logger log = LoggerFactory.getLogger(CalendarCoursesEventProvider.class);

    public CalendarCoursesEventProvider(WorkSessionRepository workSessionRepository) {
        this.workSessionRepository = workSessionRepository;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        List<WorkSession> workSessions = workSessionRepository.findBetweenStartInstantAndEndInstant(start.toInstant(), end.toInstant());

        log.info(workSessions.size() + "found !");
        return workSessions.stream().map(CalendarCoursesEventBean::new).collect(Collectors.toList());
    }
}
