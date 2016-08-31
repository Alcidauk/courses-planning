package com.alcidauk.data.repository;

import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface WorkSessionRepository extends JpaRepository<WorkSession, Long> {

    @Query("select e from WorkSession e where e.type = ?1 and e.done = true")
    List<WorkSession> findDoneByType(WorkSessionType workSessionType);

    @Query("select e from WorkSession e where e.type = ?1 and e.done = false")
    List<WorkSession> findLeftByType(WorkSessionType workSessionType);

    List<WorkSession> findByType(WorkSessionType workSessionType);

    @Query("select e from WorkSession e where e.startInstant > ?1 and e.endInstant < ?2")
    List<WorkSession> findBetweenStartInstantAndEndInstant(Instant startInstant, Instant endInstant);

    @Query("select e from WorkSession e where e.startInstant > ?1 and e.endInstant < ?2 and e.type = ?3")
    List<WorkSession> findBetweenStartInstantAndEndInstantAndType(Instant startInstant, Instant endInstant, WorkSessionType type);
}
