package com.alcidauk.ui;

import com.alcidauk.data.repository.DefaultSessionRepository;
import com.alcidauk.ui.calendar.DefaultSessionEventMoveHandler;
import com.alcidauk.ui.calendar.DefaultSessionEventResizeHandler;
import com.alcidauk.ui.calendar.DefaultSessionsEventProvider;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.handler.BasicBackwardHandler;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicForwardHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Locale;

/**
 * Created by alcidauk on 29/08/16.
 */
public class DefaultSessionSettingsWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionSettingsWindow.class);

    private DefaultSessionRepository defaultSessionRepository;

    private VerticalLayout mainLayout;

    private Calendar calendar;

    public DefaultSessionSettingsWindow(DefaultSessionRepository defaultSessionRepository) {
        this.defaultSessionRepository = defaultSessionRepository;
    }

    public void init(){
        calendar = new Calendar();
        calendar.setLocale(Locale.FRENCH);
        calendar.setFirstVisibleHourOfDay(6);
        calendar.setLastVisibleHourOfDay(21);

        // set empty string to avoid showing date, except day of week
        calendar.setWeeklyCaptionFormat("");

        DefaultSessionsEventProvider calendarProvider = new DefaultSessionsEventProvider(defaultSessionRepository, calendar.getStartDate());
        calendar.setEventProvider(calendarProvider);

        calendar.setHandler(new DefaultSessionEventMoveHandler());
        calendar.setHandler(new DefaultSessionEventResizeHandler());

        // avoid changing week
        calendar.setHandler(new BasicBackwardHandler() {
            protected void setDates(CalendarComponentEvents.BackwardEvent event,
                                    Date start, Date end) {
            }}
        );
        calendar.setHandler(new BasicForwardHandler() {
            protected void setDates(CalendarComponentEvents.ForwardEvent event,
                                    Date start, Date end) {
            }}
        );
        calendar.setHandler(new BasicDateClickHandler() {
            protected void setDates(CalendarComponentEvents.DateClickEvent event,
                                    Date start, Date end) {
            }
        });

        calendar.addStyleName("calendar");

        mainLayout = new VerticalLayout();

        mainLayout.addComponent(calendar);

        mainLayout.setMargin(true);

        setCaption("Modifier le planning des disponibilités par défaut");
        setContent(mainLayout);
    }
}
