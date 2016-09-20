package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.ui.calendar.CalendarUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 18/09/16.
 */
public class SessionGenerator {

    private static final long MAX_HOURS_TO_PLACE = 4;
    private static final long CESURE_BETWEEN_SESSIONS_HOURS = 1;

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

        List<PlanningPeriodEventType> tmpPeriodEventTypes = periodEventTypes.stream().filter(planningPeriodEventType ->
                planningPeriodEventType.getPeriodDuration().toHours() > 0).collect(Collectors.toList());

        Map<PlanningPeriodEventType, Long> hoursPlacedByPeriodEventType = new HashMap<>();

        int periodEventTypeIndex = 0;

        for (DurationTime durationTime : availablePeriods) {
            long remainingHoursToPlace = durationTime.getDuration().toHours();
            LocalDateTime startDateTime = durationTime.getStartDateTime();

            while(tmpPeriodEventTypes.size() > periodEventTypeIndex
                    && remainingHoursToPlace > 0
                    && startDateTime.isBefore(durationTime.getEndDateTime())){
                PlanningPeriodEventType periodEventTypeToPlace = tmpPeriodEventTypes.get(periodEventTypeIndex);

                Long hoursAlreadyPlaced = hoursPlacedByPeriodEventType.get(periodEventTypeToPlace);
                if(hoursAlreadyPlaced == null){
                    hoursAlreadyPlaced = 0L;
                }

                long hoursToPlace = Math.min(durationTime.getDuration().toHours(),
                        Math.min(periodEventTypeToPlace.getPeriodDuration().toHours() - hoursAlreadyPlaced, MAX_HOURS_TO_PLACE));

                LocalDateTime newStartDateTime = startDateTime.plus(hoursToPlace, ChronoUnit.HOURS);
                workSessions.add(new WorkSession(startDateTime.toInstant(ZoneOffset.UTC),
                        newStartDateTime.toInstant(ZoneOffset.UTC),
                        periodEventTypeToPlace.getType(),
                        false)
                );

                // update hours placed for periodtype
                long totalOfHoursPlaced = hoursAlreadyPlaced + hoursToPlace;
                hoursPlacedByPeriodEventType.put(periodEventTypeToPlace, totalOfHoursPlaced);

                // remove from periodtype that have hours to place if all has been placed
                if(totalOfHoursPlaced >= periodEventTypeToPlace.getPeriodDuration().toHours()){
                    tmpPeriodEventTypes.remove(periodEventTypeToPlace);
                }

                startDateTime = newStartDateTime.plus(CESURE_BETWEEN_SESSIONS_HOURS, ChronoUnit.HOURS);
                remainingHoursToPlace -= hoursToPlace;

                if(periodEventTypeIndex == tmpPeriodEventTypes.size() -1){
                    periodEventTypeIndex = 0;
                } else {
                    periodEventTypeIndex++;
                }
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
