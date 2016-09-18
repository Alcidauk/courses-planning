package com.alcidauk.data.repository;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.User;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface PlanningPeriodRepository extends JpaRepository<PlanningPeriod, Long> {

    PlanningPeriod findByStartInstantAndEndInstantAndUser(Instant startInstant, Instant endInstant, User user);

}
