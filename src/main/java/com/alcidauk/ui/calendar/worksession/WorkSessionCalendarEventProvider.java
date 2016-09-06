package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.repository.DefaultSessionRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
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
public class WorkSessionCalendarEventProvider implements CalendarEventProvider {

    private WorkSessionRepository workSessionRepository;
    private PlanningPeriodRepository planningPeriodRepository;
    private DefaultSessionRepository defaultSessionRepository;

    private static final Logger log = LoggerFactory.getLogger(WorkSessionCalendarEventProvider.class);

    public WorkSessionCalendarEventProvider(WorkSessionRepository workSessionRepository,
                                            PlanningPeriodRepository planningPeriodRepository,
                                            DefaultSessionRepository defaultSessionRepository) {
        this.workSessionRepository = workSessionRepository;
        this.planningPeriodRepository = planningPeriodRepository;
        this.defaultSessionRepository = defaultSessionRepository;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        List<WorkSession> workSessions = workSessionRepository.findBetweenStartInstantAndEndInstant(start.toInstant(), end.toInstant());
        PlanningPeriod planningPeriod = planningPeriodRepository.findByStartInstantAndEndInstant(start.toInstant(), end.toInstant());

        if(!planningPeriod.isDefaultSessionsGenerated()){
            generateDefaultSessionsAsEvents();
            planningPeriod.setDefaultSessionsGenerated(true);
            planningPeriodRepository.save(planningPeriod);
        }

        log.info(workSessions.size() + "found !");
        return workSessions.stream().map(WorkSessionCalendarEventBean::new).collect(Collectors.toList());
    }

    private void generateDefaultSessionsAsEvents() {
        List<DefaultUnavailabilitySession> defaultUnavailabilitySessions = defaultSessionRepository.findAll();
        for(DefaultUnavailabilitySession defaultUnavailabilitySession : defaultUnavailabilitySessions){
            WorkSession workSession = new WorkSession(null, null, null, false);
        }
    }
}
