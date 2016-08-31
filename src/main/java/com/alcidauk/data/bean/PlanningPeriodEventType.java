package com.alcidauk.data.bean;


import javax.persistence.*;
import java.time.Duration;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class PlanningPeriodEventType {

    @Id
    @GeneratedValue
    private Long id;

    private Duration periodDuration;

    @ManyToOne(fetch = FetchType.EAGER)
    private CalendarCoursesEventType type;

    public PlanningPeriodEventType() {
    }

    public PlanningPeriodEventType(Duration periodDuration, CalendarCoursesEventType type) {
        this.periodDuration = periodDuration;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Duration getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(Duration periodDuration) {
        this.periodDuration = periodDuration;
    }

    public CalendarCoursesEventType getType() {
        return type;
    }

    public void setType(CalendarCoursesEventType type) {
        this.type = type;
    }
}
