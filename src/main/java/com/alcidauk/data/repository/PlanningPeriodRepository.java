package com.alcidauk.data.repository;

import com.alcidauk.data.bean.PlanningPeriod;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface PlanningPeriodRepository extends JpaRepository<PlanningPeriod, Long> {

    List<PlanningPeriod> findByStartInstantAndEndInstant(Instant startInstant, Instant endInstant);

}
