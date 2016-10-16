package com.alcidauk.data.bean;


import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by alcidauk on 20/08/16.
 */
@Entity
public class DefaultUnavailabilitySession {

    @Id
    @GeneratedValue
    private Long id;

    private int dayOfWeek;

    private int startHour;

    private Duration duration;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private Instant lastModification;

    public DefaultUnavailabilitySession() {
    }

    public DefaultUnavailabilitySession(User user) {
        this.user = user;
    }

    public DefaultUnavailabilitySession(int dayOfWeek, int startHour, Duration duration, User user) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.duration = duration;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getLastModification() {
        return lastModification;
    }

    public void setLastModification(Instant lastModification) {
        this.lastModification = lastModification;
    }
}
