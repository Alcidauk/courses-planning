package com.alcidauk.data.bean;


import com.alcidauk.app.CoursesSpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class CalendarCoursesEventType {

    private static final Logger log = LoggerFactory.getLogger(CoursesSpringApplication.class);


    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    private List<WorkSession> workSessions;

    public CalendarCoursesEventType() {
    }

    public CalendarCoursesEventType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<WorkSession> getWorkSessions() {
        return workSessions;
    }

    public void setWorkSessions(List<WorkSession> workSessions) {
        this.workSessions = workSessions;
    }
}
