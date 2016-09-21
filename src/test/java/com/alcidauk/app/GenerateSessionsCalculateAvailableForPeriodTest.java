package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.ui.calendar.CalendarUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;

/**
 * Created by alcidauk on 18/09/16.
 */
public class GenerateSessionsCalculateAvailableForPeriodTest {

    private WorkSessionType unavailableType;
    private WorkSessionType coursesType;
    private WorkSessionType diplomaType;
    private PlanningPeriod planningPeriod;

    private Instant startPeriodInstant;
    private Instant endPeriodInstant;

    private LocalDate today;
    private LocalTime midnight;

    private LocalDateTime oldNow;
    
    @Mock
    private WorkSessionRepository workSessionRepository;

    @Before
    public void setUp(){
        midnight = LocalTime.MIDNIGHT;
        today = LocalDate.now(ZoneId.systemDefault());
        
        oldNow = LocalDateTime.now().minusDays(1);

        startPeriodInstant = LocalDateTime.of(today, midnight).toInstant(ZoneOffset.UTC);
        endPeriodInstant = LocalDateTime.of(today, LocalTime.of(23, 59)).toInstant(ZoneOffset.UTC);

        unavailableType = new WorkSessionType("unavailable", true);
        unavailableType.setId(1L);
        coursesType = new WorkSessionType("courses", true);
        coursesType.setId(2L);
        diplomaType = new WorkSessionType("diploma", true);
        diplomaType.setId(3L);

        planningPeriod = new PlanningPeriod(startPeriodInstant, endPeriodInstant, false, null);

        workSessionRepository = Mockito.mock(WorkSessionRepository.class);
    }

    private void withNoSessionsDone() {
        doReturn(new ArrayList<>()).when(workSessionRepository)
                .findDoneBetweenStartInstantAndEndInstant(same(planningPeriod.getStartInstant()), same(planningPeriod.getEndInstant()), any());
    }

    /*
     * available durationTimes calculation tests
     */

    @Test
    public void testAvailableSessionsCalculation_noAvailable(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions =
                new ArrayList<>(Collections.singletonList(new WorkSession(startPeriodInstant, endPeriodInstant, unavailableType, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);
        assertEquals(new ArrayList<>(), sessionGenerator.calculateAvailablePeriods());
    }

    @Test
    public void testAvailableSessionsCalculation_allAvailable(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>();

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableEndAtEndOfPeriod(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(
                Collections.singletonList(new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(5L)), null, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(5L))),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableStartAtStartOfPeriod(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(
                Collections.singletonList(
                        new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), endPeriodInstant, null, false)));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant),
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(5L)))),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_oneAvailableAfterTwoConsecutiveUnavailable(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(Arrays.asList(
                new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(5L)), null, false),
                new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), startPeriodInstant.plus(Duration.ofHours(7L)), null, false)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);

        assertEquals(1, sessionGenerator.calculateAvailablePeriods().size());
        assertEquals(new DurationTime(
                CalendarUtils.getLocalDateTimeFromInstant(startPeriodInstant.plus(Duration.ofHours(7L))),
                CalendarUtils.getLocalDateTimeFromInstant(endPeriodInstant)),
                sessionGenerator.calculateAvailablePeriods().get(0)
        );
    }

    @Test
    public void testAvailableSessionsCalculation_twoAvailable(){
        withNoSessionsDone();
        ArrayList<WorkSession> unavailableWorkSessions = new ArrayList<>(Arrays.asList(
                new WorkSession(startPeriodInstant, startPeriodInstant.plus(Duration.ofHours(4L)), null, false),
                new WorkSession(startPeriodInstant.plus(Duration.ofHours(5L)), startPeriodInstant.plus(Duration.ofHours(7L)), null, false)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, unavailableWorkSessions, null, workSessionRepository, oldNow);
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
}
