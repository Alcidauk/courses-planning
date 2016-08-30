package com.alcidauk.data.repository;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.bean.CalendarCoursesEventType;
import com.alcidauk.data.bean.User;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@SpringComponent
@UIScope
public interface CalendarCoursesEventRepository extends JpaRepository<CalendarCoursesEvent, Long> {

    @Query("select e from CalendarCoursesEvent e where e.type = ?1 and e.done = true")
    List<CalendarCoursesEvent> findDoneByType(CalendarCoursesEventType coursesEventType);

    @Query("select e from CalendarCoursesEvent e where e.type = ?1 and e.done = false")
    List<CalendarCoursesEvent> findLeftByType(CalendarCoursesEventType coursesEventType);

    List<CalendarCoursesEvent> findByType(CalendarCoursesEventType coursesEventType);

}
