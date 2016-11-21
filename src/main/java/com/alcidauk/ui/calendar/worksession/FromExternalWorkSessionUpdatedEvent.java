package com.alcidauk.ui.calendar.worksession;

import com.vaadin.ui.Component;

import java.time.Instant;

/**
 * Created by alcidauk on 24/08/16.
 */
public class FromExternalWorkSessionUpdatedEvent extends Component.Event{

    private Instant startInstant;
    private Instant endInstant;

    public FromExternalWorkSessionUpdatedEvent(Component source, Instant startInstant, Instant endInstant) {
        super(source);
        this.startInstant = startInstant;
        this.endInstant = endInstant;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }
}
