package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.repository.WorkSessionRepository;
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

    private WorkSessionRepository workSessionRepository;

    private LocalDateTime now;

    public SessionGenerator(PlanningPeriod period, List<WorkSession> unavailableWorkSessions,
                            List<PlanningPeriodEventType> periodEventTypes, WorkSessionRepository workSessionRepository,
                            LocalDateTime now) {
        this.period = period;
        this.unavailableWorkSessions = unavailableWorkSessions;
        this.periodEventTypes = periodEventTypes;
        this.workSessionRepository = workSessionRepository;
        this.now = now;
    }

    public SessionGenerator(PlanningPeriod period, List<WorkSession> unavailableWorkSessions,
                            List<PlanningPeriodEventType> periodEventTypes, WorkSessionRepository workSessionRepository) {
        this.period = period;
        this.unavailableWorkSessions = unavailableWorkSessions;
        this.periodEventTypes = periodEventTypes;
        this.workSessionRepository = workSessionRepository;
        this.now = LocalDateTime.now();
    }

    public List<WorkSession> generateSessions() {
        if(periodEventTypes.isEmpty()){
            return new ArrayList<>();
        }

        cleanExistingSessionsAfterNow();

        List<DurationTime> availablePeriods = calculateAvailablePeriods();

        return generateWorkSessions(availablePeriods);
    }

    /**
     * remove sessions that will be erased by the new generation of sessions between now and the end of the period
     */
    private void cleanExistingSessionsAfterNow() {
        List<WorkSession> sessionsToRemove =
                workSessionRepository.findNotSystemBetweenStartInstantAndEndInstant(now.toInstant(ZoneOffset.UTC), period.getEndInstant());
        workSessionRepository.delete(sessionsToRemove);
    }

    /**
     * Generate workSessions after now depending of the number of hours to place per type of worksessions
     * This generation take in account:
     *   - the number of hours already done in the period for each session type
     *   - the "now" value i.e. no sessions are generated before this dateTime
     *   - a maximum duration of each workSession
     *   - between every session, a remaining available period
     *   - turns between session types
     *     => means that if there are to session type that have hours to place:
     *       -> the first session will be of type 0
     *       -> the next one will be of type 1
     *       -> the next one will be back to type 0
     *       -> etc.
     * @param availablePeriods periods where to place sessions
     * @return a list of generated worksessions
     */
    List<WorkSession> generateWorkSessions(List<DurationTime> availablePeriods) {
        List<WorkSession> workSessions = new ArrayList<>();

        Map<PlanningPeriodEventType, Long> hoursPlacedByPeriodEventType = initializeHoursAlreadyPlaced(periodEventTypes);

        List<PlanningPeriodEventType> tmpPeriodEventTypes = periodEventTypes.stream().filter(planningPeriodEventType ->
                planningPeriodEventType.getPeriodDuration().toHours() > hoursPlacedByPeriodEventType.get(planningPeriodEventType)
        ).collect(Collectors.toList());

        availablePeriods.stream().filter(durationTime
                -> durationTime.getEndDateTime().isAfter(now)).collect(Collectors.toList());

        int periodEventTypeIndex = 0;

        for (DurationTime durationTime : availablePeriods) {
            if(tmpPeriodEventTypes.size() > 0) {
                long remainingHoursToPlace = durationTime.getDuration().toHours() -
                        hoursPlacedByPeriodEventType.get(tmpPeriodEventTypes.get(periodEventTypeIndex));

                LocalDateTime startDateTime = durationTime.getStartDateTime().isAfter(now) ? durationTime.getStartDateTime() :
                        now.truncatedTo(ChronoUnit.HOURS);

                while (tmpPeriodEventTypes.size() > periodEventTypeIndex
                        && remainingHoursToPlace > 0
                        && startDateTime.isBefore(durationTime.getEndDateTime())) {
                    PlanningPeriodEventType periodEventTypeToPlace = tmpPeriodEventTypes.get(periodEventTypeIndex);

                    Long hoursAlreadyPlaced = hoursPlacedByPeriodEventType.get(periodEventTypeToPlace);
                    if (hoursAlreadyPlaced == null) {
                        hoursAlreadyPlaced = 0L;
                    }

                    long hoursToPlace = Math.min(Duration.between(startDateTime, durationTime.getEndDateTime()).toHours(),
                            Math.min(durationTime.getDuration().toHours(),
                                    Math.min(periodEventTypeToPlace.getPeriodDuration().toHours() - hoursAlreadyPlaced, MAX_HOURS_TO_PLACE)));

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
                    if (totalOfHoursPlaced >= periodEventTypeToPlace.getPeriodDuration().toHours()) {
                        tmpPeriodEventTypes.remove(periodEventTypeToPlace);
                    }

                    startDateTime = newStartDateTime.plus(CESURE_BETWEEN_SESSIONS_HOURS, ChronoUnit.HOURS);
                    remainingHoursToPlace -= hoursToPlace;

                    if (periodEventTypeIndex == tmpPeriodEventTypes.size() - 1) {
                        periodEventTypeIndex = 0;
                    } else {
                        periodEventTypeIndex++;
                    }
                }
            }
        }

        return workSessions;
    }

    /**
     * Depending on periodEventTypes that are given, take the type of session of each one, and get sessions for the period
     * and the type. Then subtract hours of these done sessions to remaining hours to place.
     * @param periodEventTypes the list of types we want to get session type and get done sessions
     * @return a map that store for each type, the number of hours done in the period
     */
    private Map<PlanningPeriodEventType, Long> initializeHoursAlreadyPlaced(List<PlanningPeriodEventType> periodEventTypes) {
        Map<PlanningPeriodEventType, Long> hoursAlreadyDone = new HashMap<>();

        for(PlanningPeriodEventType planningPeriodEventType : periodEventTypes){
            List<WorkSession> doneForTypeInPeriod = workSessionRepository.findDoneBetweenStartInstantAndEndInstant(
                    period.getStartInstant(), period.getEndInstant(), planningPeriodEventType.getType()
            );

            long hoursDone = 0;
            for(WorkSession workSession : doneForTypeInPeriod){
                hoursDone += Duration.between(workSession.getStartInstant(), workSession.getEndInstant()).toHours();
            }

            hoursAlreadyDone.put(planningPeriodEventType, hoursDone);
        }

        return hoursAlreadyDone;
    }

    /**
     * Apply a sort of inverse of a planningPeriod : depending on the unavailable sessions that have been set,
     * calculate all the remaining periods in the sessions, also called available periods.
     * @return a list of available periods
     */
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
