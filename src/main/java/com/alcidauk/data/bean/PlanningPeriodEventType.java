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
    private WorkSessionType type;

    @ManyToOne(fetch = FetchType.EAGER)
    private PlanningPeriod period;

    public PlanningPeriodEventType() {
    }

    public PlanningPeriodEventType(Duration periodDuration, WorkSessionType type, PlanningPeriod period) {
        this.periodDuration = periodDuration;
        this.type = type;
        this.period = period;
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

    public WorkSessionType getType() {
        return type;
    }

    public void setType(WorkSessionType type) {
        this.type = type;
    }

    public PlanningPeriod getPeriod() {
        return period;
    }

    public void setPeriod(PlanningPeriod period) {
        this.period = period;
    }
}
