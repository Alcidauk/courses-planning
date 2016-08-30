package com.alcidauk.data.bean;


import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class PlanningPeriod {

    @Id
    @GeneratedValue
    private Long id;

    private Instant startInstant;

    private Instant endInstant;

    private Duration periodDuration;

    @ManyToOne(fetch = FetchType.EAGER)
    private CalendarCoursesEventType type;

    public PlanningPeriod() {
    }

    public PlanningPeriod(Instant startInstant, Instant endInstant, Duration periodDuration, CalendarCoursesEventType type) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.periodDuration = periodDuration;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
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
