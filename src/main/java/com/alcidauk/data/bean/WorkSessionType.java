package com.alcidauk.data.bean;


import javax.persistence.*;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class WorkSessionType {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.EAGER)
    private List<WorkSession> workSessions;

    private boolean system;

    public WorkSessionType() {
    }

    public WorkSessionType(String name, boolean system) {
        this.name = name;
        this.system = system;
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

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
