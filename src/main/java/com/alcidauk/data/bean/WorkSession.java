package com.alcidauk.data.bean;


import javax.persistence.*;
import java.time.Instant;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class WorkSession {

    @Id
    @GeneratedValue
    private Long id;

    private Instant startInstant;

    private Instant endInstant;

    private boolean done;

    private String title;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private WorkSessionType type;

    public WorkSession() {
    }

    public WorkSession(Instant startInstant, Instant endInstant, WorkSessionType type, boolean isDone) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.type = type;
        this.done = isDone;
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

    public WorkSessionType getType() {
        return type;
    }

    public void setType(WorkSessionType type) {
        this.type = type;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
