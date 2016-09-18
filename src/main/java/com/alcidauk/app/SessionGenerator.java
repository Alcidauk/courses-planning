package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.ui.calendar.CalendarUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 18/09/16.
 */
public class SessionGenerator {

    private PlanningPeriod period;
    private List<WorkSession> unavailableWorkSessions;
    private List<PlanningPeriodEventType> periodEventTypes;

    public SessionGenerator(PlanningPeriod period, List<WorkSession> unavailableWorkSessions, List<PlanningPeriodEventType> periodEventTypes) {
        this.period = period;
        this.unavailableWorkSessions = unavailableWorkSessions;
        this.periodEventTypes = periodEventTypes;
    }

    // TODO finish it when generateWorkSessions is finished. Has to return result
    public List<WorkSession> generateSessions() {
        if(periodEventTypes.isEmpty()){
            return new ArrayList<>();
        }

        List<DurationTime> availablePeriods = calculateAvailablePeriods();

        List<WorkSession> generatedWorkSessions = generateWorkSessions(availablePeriods);

        return null;
    }

    // TODO finish it => add workSessions while there is place or while there are hours to schedule
    List<WorkSession> generateWorkSessions(List<DurationTime> availablePeriods) {
        List<WorkSession> workSessions = new ArrayList<>();

        List<PlanningPeriodEventType> tmpPeriodEventTypes = periodEventTypes.stream().filter(new Predicate<PlanningPeriodEventType>() {
            @Override
            public boolean test(PlanningPeriodEventType planningPeriodEventType) {
                return planningPeriodEventType.getPeriodDuration().toHours() > 0;
            }
        }).collect(Collectors.toList());

        for (DurationTime durationTime : availablePeriods) {
            if (durationTime.getDuration().toHours() != 0) {

            }
        }

        return workSessions;
    }

    List<DurationTime> calculateAvailablePeriods() {
        Collections.sort(unavailableWorkSessions, (workSession1, workSession2) ->
                workSession1.getEndInstant().isBefore(workSession2.getEndInstant()) ? -1 : 1);

        List<DurationTime> availablePeriods = new ArrayList<>();
        if(unavailableWorkSessions.size() == 0 || unavailableWorkSessions.get(0).getStartInstant() != period.getStartInstant()){
            LocalDateTime endOfPeriod;
            if(unavailableWorkSessions.size() == 0){
                endOfPeriod = CalendarUtils.getLocalDateTimeFromInstant(period.getEndInstant());
            } else {
                endOfPeriod = CalendarUtils.getLocalDateTimeFromInstant(unavailableWorkSessions.get(0).getStartInstant());
            }

            availablePeriods.add(new DurationTime(
                    CalendarUtils.getLocalDateTimeFromInstant(period.getStartInstant()),
                    endOfPeriod
            ));
        }

        for(WorkSession workSession : unavailableWorkSessions){
            int nextIndex = unavailableWorkSessions.indexOf(workSession) + 1;

            LocalDateTime firstDate = CalendarUtils.getLocalDateTimeFromInstant(workSession.getEndInstant());
            LocalDateTime secondDate;
            if(nextIndex < unavailableWorkSessions.size()) {
                secondDate = CalendarUtils.getLocalDateTimeFromInstant(unavailableWorkSessions.get(nextIndex).getStartInstant());
            } else {
                secondDate = CalendarUtils.getLocalDateTimeFromInstant(period.getEndInstant());
            }

            DurationTime availableDurationTimeToAdd = getDurationWithDateIfNonZero(firstDate, secondDate);
            if(availableDurationTimeToAdd != null){
                availablePeriods.add(availableDurationTimeToAdd);
            }
        }
        return availablePeriods;
    }

    private DurationTime getDurationWithDateIfNonZero(LocalDateTime firstDate, LocalDateTime secondDate) {
        if (!Duration.between(firstDate, secondDate).isZero()) {
            return new DurationTime(firstDate, secondDate);
        }
        return null;
    }
}
