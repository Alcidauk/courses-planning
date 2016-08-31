package com.alcidauk.data.repository;

import com.alcidauk.data.bean.PlanningPeriod;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface PlanningPeriodRepository extends JpaRepository<PlanningPeriod, Long> {

    PlanningPeriod findByStartInstantAndEndInstant(Instant startInstant, Instant endInstant);

}
