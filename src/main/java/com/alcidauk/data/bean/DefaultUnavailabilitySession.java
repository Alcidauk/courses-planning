package com.alcidauk.data.bean;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.Duration;

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

    public DefaultUnavailabilitySession() {
    }

    public DefaultUnavailabilitySession(int dayOfWeek, int startHour, Duration duration) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.duration = duration;
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
}
