package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.bean.*;
import com.alcidauk.data.repository.DefaultUnavailabilitySessionRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.alcidauk.ui.CoursesUI;
import com.alcidauk.ui.calendar.CalendarUtils;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 30/08/16.
 */
public class WorkSessionCalendarEventProvider implements CalendarEventProvider {

    private WorkSessionRepository workSessionRepository;
    private WorkSessionTypeRepository workSessionTypeRepository;
    private PlanningPeriodRepository planningPeriodRepository;
    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;

    private Calendar calendar;

    private static final Logger log = LoggerFactory.getLogger(WorkSessionCalendarEventProvider.class);

    private PlanningPeriod planningPeriod;

    public WorkSessionCalendarEventProvider(WorkSessionRepository workSessionRepository,
                                            PlanningPeriodRepository planningPeriodRepository,
                                            DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository,
                                            WorkSessionTypeRepository workSessionTypeRepository,
                                            Calendar calendar) {
        this.workSessionRepository = workSessionRepository;
        this.workSessionTypeRepository = workSessionTypeRepository;
        this.planningPeriodRepository = planningPeriodRepository;
        this.defaultUnavailabilitySessionRepository = defaultUnavailabilitySessionRepository;
        this.calendar = calendar;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        planningPeriod = planningPeriodRepository.findByStartInstantAndEndInstant(
                Instant.ofEpochSecond(start.toInstant().getEpochSecond()), Instant.ofEpochSecond(end.toInstant().getEpochSecond()));

        if(planningPeriod == null){
            log.info(String.format("Creating planning period for dates %s to %s.", start.toString(), end.toString()));
            planningPeriod = createPlanningPeriod(Instant.ofEpochSecond(start.toInstant().getEpochSecond()),
                    Instant.ofEpochSecond(end.toInstant().getEpochSecond()));
        }
        firePlanningPeriodChanged();

        if(!planningPeriod.isDefaultSessionsGenerated()){
            WorkSessionType unavailableType = workSessionTypeRepository.findByName("unavailable");
            generateDefaultSessionsAsEvents(start, unavailableType);
            fireEventChanged(unavailableType);
            planningPeriod.setDefaultSessionsGenerated(true);
            planningPeriodRepository.save(planningPeriod);
        }

        List<WorkSession> workSessions = workSessionRepository.findBetweenStartInstantAndEndInstant(start.toInstant(), end.toInstant());
        log.info(workSessions.size() + "found !");
        return workSessions.stream().map(WorkSessionCalendarEventBean::new).collect(Collectors.toList());
    }

    private PlanningPeriod createPlanningPeriod(Instant startInstant, Instant endInstant) {
        PlanningPeriod planningPeriod = new PlanningPeriod(startInstant, endInstant, getPlanningPeriodTypes(), false);
        planningPeriodRepository.save(planningPeriod);
        return planningPeriod;
    }

    private List<PlanningPeriodEventType> getPlanningPeriodTypes() {
        return null;
    }

    private void generateDefaultSessionsAsEvents(Date start, WorkSessionType unavailableWorkSessionType) {
        List<DefaultUnavailabilitySession> defaultUnavailabilitySessions = defaultUnavailabilitySessionRepository.findAll();
        for(DefaultUnavailabilitySession defaultUnavailabilitySession : defaultUnavailabilitySessions){
            Instant sessionStart = CalendarUtils.getDateInWeek(start, defaultUnavailabilitySession.getDayOfWeek(), defaultUnavailabilitySession.getStartHour());
            Instant sessionEnd = CalendarUtils.getDateInWeek(start, defaultUnavailabilitySession.getDayOfWeek(),
                    defaultUnavailabilitySession.getStartHour(), defaultUnavailabilitySession.getDuration());
            WorkSession workSession = new WorkSession(sessionStart, sessionEnd, unavailableWorkSessionType, false);
            workSessionRepository.save(workSession);
        }
    }

    private void firePlanningPeriodChanged() {
        for(ShownPlanningPeriodListener listener :  ((CoursesUI) UI.getCurrent()).getShownPlanningPeriodChangedListeners()){
            listener.update(new ShowPlanningPeriodChangedEvent(calendar, planningPeriod));
        }
    }

    private void fireEventChanged(WorkSessionType unavailableType) {
        for(WorkSessionTypeListener listener :  ((CoursesUI) UI.getCurrent()).getWorkSessionTypeListeners()){
            listener.update(new WorkSessionTypeUpdatedEvent(calendar, unavailableType));
        }
    }
}