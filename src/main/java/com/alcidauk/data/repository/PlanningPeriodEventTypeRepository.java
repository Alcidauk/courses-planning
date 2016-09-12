package com.alcidauk.data.repository;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSessionType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface PlanningPeriodEventTypeRepository extends JpaRepository<PlanningPeriodEventType, Long> {

    List<PlanningPeriodEventType> findByTypeAndPeriod(WorkSessionType type, PlanningPeriod period);

}
