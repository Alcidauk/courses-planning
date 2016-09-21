package com.alcidauk.app;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;

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
     * workSessions generation tests
     */

    @Test
    public void testNoSessionsToPlanGeneration(){
        withNoSessionsDone();
        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), new ArrayList<>(), workSessionRepository, oldNow);
        assertEquals(new ArrayList<>(), sessionGenerator.generateSessions());
    }

    @Test
    public void testNoPlaceToGenerateWorkSessions(){
        withNoSessionsDone();
        List<PlanningPeriodEventType> planningPeriodEventTypes = new ArrayList<>(Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        ));

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        assertEquals(new ArrayList<>(), sessionGenerator.generateWorkSessions(new ArrayList<>()));
    }

    @Test
    public void testNoWorkSessionsToGenerate(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.now();
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(3, ChronoUnit.HOURS))
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), new ArrayList<>(), workSessionRepository, oldNow);
        assertEquals(new ArrayList<>(), sessionGenerator.generateWorkSessions(availableSessions));
    }

    @Test
    public void test_oneWorkSessionsToGenerate(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);
        assertEquals(1, workSessionsGenerated.size());
        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
    }

    @Test
    public void test_oneWorkSessionsToGenerate_notEnoughPlace(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(2, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(1, workSessionsGenerated.size());
        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(2, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
    }

    @Test
    public void test_oneWorkSessionsToGenerate_notEnoughPlace_nowAt1(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(2, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, now.plus(1, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(1, workSessionsGenerated.size());
        assertEquals(now.plus(1, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(2, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inSameAvailableSession_withSameType(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());

        assertEquals(now.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
    }

    @Test
    public void test_twoWorkSessionsToGenerate_inSameAvailableSession_withDifferentTypes(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Arrays.asList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod),
                new PlanningPeriodEventType(Duration.ofHours(10), diplomaType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
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
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Arrays.asList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS)),
                new DurationTime(now.plus(10, ChronoUnit.HOURS), now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
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
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Arrays.asList(
                new DurationTime(now, now.plus(5, ChronoUnit.HOURS)),
                new DurationTime(now.plus(10, ChronoUnit.HOURS), now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Arrays.asList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod),
                new PlanningPeriodEventType(Duration.ofHours(10), diplomaType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
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
    public void test_twoWorkSessionsToGenerate_inDifferentAvailableSessions_withDifferentTypes_nowAt6(){
        withNoSessionsDone();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Arrays.asList(
                new DurationTime(todayMidnight, todayMidnight.plus(5, ChronoUnit.HOURS)),
                new DurationTime(todayMidnight.plus(10, ChronoUnit.HOURS), todayMidnight.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Arrays.asList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod),
                new PlanningPeriodEventType(Duration.ofHours(10), diplomaType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, todayMidnight.plus(4, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(todayMidnight.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(todayMidnight.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(todayMidnight.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(todayMidnight.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(diplomaType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_oneWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(10, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(2), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(1, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(2, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());
    }

    @Test
    public void test_severalWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded(){
        withNoSessionsDone();
        LocalDateTime now = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(now, now.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(8), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(), planningPeriodEventTypes, workSessionRepository, oldNow);
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(now.toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(now.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(now.plus(5, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(now.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_oneWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded_NowAt10(){
        withNoSessionsDone();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(todayMidnight, todayMidnight.plus(15, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(8), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, todayMidnight.plus(10, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(1, workSessionsGenerated.size());

        assertEquals(todayMidnight.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(todayMidnight.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());
    }

    @Test
    public void test_severalWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded_NowAt10(){
        withNoSessionsDone();
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);
        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(todayMidnight, todayMidnight.plus(20, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(8), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, todayMidnight.plus(10, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(todayMidnight.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(todayMidnight.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(todayMidnight.plus(15, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(todayMidnight.plus(19, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(1).getType());
    }

    @Test
    public void test_severalWorkSessionsToGenerate_inSameAvailableSession_withSameType_noMoreSessionsToPlace_NowAt10_withDoneSessions(){
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);

        doReturn(Arrays.asList(
                new WorkSession(todayMidnight.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        todayMidnight.plus(8, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        coursesType, true),
                new WorkSession(todayMidnight.plus(9, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        todayMidnight.plus(13, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        coursesType, true)
        ))
                .when(workSessionRepository)
                .findDoneBetweenStartInstantAndEndInstant(same(planningPeriod.getStartInstant()), same(planningPeriod.getEndInstant()), any());

        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(todayMidnight, todayMidnight.plus(20, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(8), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, todayMidnight.plus(10, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(0, workSessionsGenerated.size());
    }

    @Test
    public void test_severalWorkSessionsToGenerate_inSameAvailableSession_withSameType_morePlaceThanNeeded_NowAt10_withDoneSessions(){
        LocalDateTime todayMidnight = LocalDateTime.of(today, midnight);

        doReturn(Arrays.asList(
                new WorkSession(todayMidnight.plus(4, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        todayMidnight.plus(6, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        coursesType, true),
                new WorkSession(todayMidnight.plus(7, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        todayMidnight.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC),
                        coursesType, true)
        ))
                .when(workSessionRepository)
                .findDoneBetweenStartInstantAndEndInstant(same(planningPeriod.getStartInstant()), same(planningPeriod.getEndInstant()), any());

        List<DurationTime> availableSessions = Collections.singletonList(
                new DurationTime(todayMidnight, todayMidnight.plus(20, ChronoUnit.HOURS))
        );

        List<PlanningPeriodEventType> planningPeriodEventTypes = Collections.singletonList(
                new PlanningPeriodEventType(Duration.ofHours(10), coursesType, planningPeriod)
        );

        SessionGenerator sessionGenerator = new SessionGenerator(planningPeriod, new ArrayList<>(),
                planningPeriodEventTypes, workSessionRepository, todayMidnight.plus(10, ChronoUnit.HOURS));
        List<WorkSession> workSessionsGenerated = sessionGenerator.generateWorkSessions(availableSessions);

        assertEquals(2, workSessionsGenerated.size());

        assertEquals(todayMidnight.plus(10, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getStartInstant());
        assertEquals(todayMidnight.plus(14, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(0).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(0).getType());

        assertEquals(todayMidnight.plus(15, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getStartInstant());
        assertEquals(todayMidnight.plus(16, ChronoUnit.HOURS).toInstant(ZoneOffset.UTC), workSessionsGenerated.get(1).getEndInstant());
        assertEquals(coursesType, workSessionsGenerated.get(1).getType());
    }
}
