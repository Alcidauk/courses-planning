package com.alcidauk.data.repository;

import com.alcidauk.data.bean.WorkSessionType;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface CalendarCoursesEventTypeRepository extends JpaRepository<WorkSessionType, Long> {

    WorkSessionType findByName(String name);

}
