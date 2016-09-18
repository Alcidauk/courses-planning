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

    @Before
    public void setUp(){
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        startPeriodInstant = LocalDateTime.of(today, midnight).toInstant(ZoneOffset.UTC);
        endPeriodInstant = LocalDateTime.of(today, LocalTime.of(23, 59)).toInstant(ZoneOffset.UTC);

        unavailableType = new WorkSessionType("unavailable", true);
        coursesType = new WorkSessionType("courses", true);
        diplomaType = new WorkSessionType("diploma", true);
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
        SessionGenerator sessionGenerator = new SessionGenerator(null, new ArrayList<>(), new ArrayList<>());
        assertEquals(new ArrayList<>(), sessionGenerator.generateWorkSessions(new ArrayList<>()));
    }

}
