package com.alcidauk.data.bean;


import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

    @OneToMany(fetch = FetchType.EAGER)
    private List<PlanningPeriodEventType> planningPeriodEventTypeList;

    private boolean defaultSessionsGenerated;

    public PlanningPeriod() {
    }

    public PlanningPeriod(Instant startInstant, Instant endInstant, List<PlanningPeriodEventType> planningPeriodEventTypeList, boolean defaultGenerated) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.planningPeriodEventTypeList = planningPeriodEventTypeList;
        this.defaultSessionsGenerated = defaultGenerated;
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

    public List<PlanningPeriodEventType> getPlanningPeriodEventTypeList() {
        return planningPeriodEventTypeList;
    }

    public void setPlanningPeriodEventTypeList(List<PlanningPeriodEventType> planningPeriodEventTypeList) {
        this.planningPeriodEventTypeList = planningPeriodEventTypeList;
    }

    public boolean isDefaultSessionsGenerated() {
        return defaultSessionsGenerated;
    }

    public void setDefaultSessionsGenerated(boolean defaultSessionsGenerated) {
        this.defaultSessionsGenerated = defaultSessionsGenerated;
    }
}
