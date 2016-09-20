package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.ui.calendar.CalendarUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by alcidauk on 18/09/16.
 */
public class GenerateSessionsForPeriodTest {

    private WorkSessionType unavailableType;
    private WorkSessionType coursesType;
    private WorkSessionType diplomaType;
    private PlanningPeriod planningPeriod;

    private Instant startPeriodInstant;
    private Instant endPeriodInstant;

    private LocalDate today;
    private LocalTime midnight;

    @Before
    public void setUp(){
        midnight = LocalTime.MIDNIGHT;
        today = LocalDate.now(ZoneId.systemDefault());

        startPeriodInstant = LocalDateTime.of(today, midnight).toInstant(ZoneOffset.UTC);
        endPeriodInstant = LocalDateTime.of(today, LocalTime.of(23, 59)).toInstant(ZoneOffset.UTC);

        unavailableType = new WorkSessionType("unavailable", true);
        unavailableType.setId(1L);
        coursesType = new WorkSessionType("courses", true);
        coursesType.setId(2L);
        diplomaType = new WorkSessionType("diploma", true);
        diplomaType.setId(3L);

        planningPeriod = new PlanningPeriod(startPeriodInstant, endPeriodInstant, false, null);
    }

    /*
     * available durationTimes calculation tests
     */

    @Test
    public void testAvailableSessionsCalculation_noAvailable(){
        ArrayList<WorkSession> unavailableWorkSessions =
                new ArrayList<>(Collections.singletonList(new WorkSession(startPeriodInstant, endPeriodInstant, unavailableType, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);
        assertEquals(new ArrayList<>(), sessionGenerator.calculateAvailablePeriods());
    }

    @Test
    public void testAvailableSessionsCalculation_allAvailable(){
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>();

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableEndAtEndOfPeriod(){
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(
                Collections.singletonList(new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(5L)), null, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(5L))),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableStartAtStartOfPeriod(){
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(
                Collections.singletonList(
                        new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), endPeriodInstant, null, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant),
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(5L)))),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableAfterTwoConsecutiveUnavailable(){
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(Arrays.asList(
                new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(5L)), null, false),
                new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), startPeriodInstant.plus(Duration.ofHours(7L)), null, false)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(7L))),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_twoAvailable(){
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(Arrays.asList(
                new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(4L)), null, false),
                new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), startPeriodInstant.plus(Duration.ofHours(7L)), null, false)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null);
        List<DurationTime> availableDurationTime = sessionGenerator.calculateAvailablePeriods();

        assertEquals(2, availableDurationTime.size());
        assertThat(availableDurationTime, Matchers.contains(
                new DurationTime(CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(4L))),
                        CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(5L)))),
                new DurationTime(CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(7L))),
                        CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant))
                )
        );
    }

    /*
     * workSessions generation tests
     */

    @Test
    public void testNoSessionsToPlanGeneration(){
        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), new ArrayList<>());
        assertEquals(new ArrayList<>(), sessionGenerator.generateSessions());
    }

    @Test
    public void testNoPlaceToGenerateWorkSessions(){
        List<PlanningPeriodEventType> planningPeriodEventTypes = new ArrayList<>(Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        assertEquals(new ArrayList<>(), sessionGenerator.generateWorkSessions(new ArrayList<>()));
    }

    @Test
    public void testNoWorkSessionsToGenerate(){
        LocalDateTime now = LocalDateTime.now();
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(3, ChronoUnit.HOURS))
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), new ArrayList<>());
        assertEquals(new ArrayList<>(), sessionGenerator.generateWorkSessions(availableSessions));
    }

    @Test
    public void test_oneWorkSessionsToGenerate(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);
        assertEquals(1, workSessionsGenerated.size());
        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inSameAvailableSession_withSameType(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());

        assertEquals(now.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inSameAvailableSession_withDifferentTypes(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Arrays.asList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod),
                new PlanningPeriodEventType(Duration.ofHours(10), diplomaType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(now.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(diplomaType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inDifferentAvailableSessions_withSameType(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Arrays.asList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS)),
                new DurationTime(now.plus(10, ChronoUnit.HOURS), now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(now.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inDifferentAvailableSessions_withDifferentTypes(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Arrays.asList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS)),
                new DurationTime(now.plus(10, ChronoUnit.HOURS), now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Arrays.asList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod),
                new PlanningPeriodEventType(Duration.ofHours(10), diplomaType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(now.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(diplomaType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_oneWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(2), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(1, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(2, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());
    }

    @Test
    public void test_severalWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded(){
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(8), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), planningPeriodEventTypes);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(now.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(1).getType());
    }

}
