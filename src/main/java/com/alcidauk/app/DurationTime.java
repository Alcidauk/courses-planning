package com.alcidauk.app;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by alcidauk on 18/09/16.
 *
 * A simple class wrapping a LocalDateTime and a duration
 * Can be used to represent every Duration that needs to be scheduled
 */
public class DurationTime {

    private LocalDateTime startDateTime;
    private Duration duration;

    /**
     * the common contructor
     * @param startDateTime will be the start time of the duration
     * @param endDateTime the end dateTime is represented in the object as a duration, so it is converted by
     *                    calculating duration between the start dateTime and this dateTime
     */
    public DurationTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.duration = Duration.between(startDateTime, endDateTime);
    }

    public LocalDateTime getStartDateTime(){
        return startDateTime;
    }

    public LocalDateTime getEndDateTime(){
        return startDateTime.plus(duration);
    }

    public void setStartDateTime(LocalDateTime startDateTime){
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime){
        this.duration = Duration.between(startDateTime, endDateTime);
    }

    public Duration getDuration(){
        return duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DurationTime that = (DurationTime) o;

        if (!startDateTime.equals(that.startDateTime)) return false;
        return duration.equals(that.duration);

    }

    @Override
    public int hashCode() {
        int result = startDateTime.hashCode();
        result = 31 * result + duration.hashCode();
        return result;
    }
}
