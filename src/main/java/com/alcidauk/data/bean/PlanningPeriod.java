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

    private Instant defaultSessionsGenerationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public PlanningPeriod() {
    }

    public PlanningPeriod(Instant startInstant, Instant endInstant, Instant defaultSessionsGenerationDate, User user) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.defaultSessionsGenerationDate = defaultSessionsGenerationDate;
        this.user = user;
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

    public Instant getDefaultSessionsGenerationDate() {
        return defaultSessionsGenerationDate;
    }

    public void setDefaultSessionsGenerationDate(Instant defaultSessionsGenerationDate) {
        this.defaultSessionsGenerationDate = defaultSessionsGenerationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
