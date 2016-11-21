package com.alcidauk.ui.calendar.defaultsession;

import com.alcidauk.app.Messages;
import com.alcidauk.data.repository.DefaultUnavailabilitySessionRepository;
import com.alcidauk.ui.calendar.defaultsession.handlers.DefaultSessionEventContextMenuHandler;
import com.alcidauk.ui.calendar.defaultsession.handlers.DefaultSessionEventDragHandler;
import com.alcidauk.ui.calendar.defaultsession.handlers.DefaultSessionEventMoveHandler;
import com.alcidauk.ui.calendar.defaultsession.handlers.DefaultSessionEventResizeHandler;
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

    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;

    private VerticalLayout mainLayout;

    private Calendar calendar;

    public DefaultSessionSettingsWindow(DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository) {
        this.defaultUnavailabilitySessionRepository = defaultUnavailabilitySessionRepository;
    }

    public void init(){
        calendar = new Calendar();
        calendar.setLocale(Locale.FRENCH);
        calendar.setFirstVisibleHourOfDay(0);
        calendar.setLastVisibleHourOfDay(24);

        // set empty string to avoid showing date, except day of week
        calendar.setWeeklyCaptionFormat("");

        DefaultSessionsEventProvider calendarProvider = new DefaultSessionsEventProvider(defaultUnavailabilitySessionRepository,
                calendar.getStartDate(), calendar);
        calendar.setEventProvider(calendarProvider);

        calendar.setHandler(new DefaultSessionEventMoveHandler());
        calendar.setHandler(new DefaultSessionEventResizeHandler());
        calendar.setHandler(new DefaultSessionEventDragHandler());

        calendar.addActionHandler(new DefaultSessionEventContextMenuHandler());

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
        calendar.setSizeFull();

        mainLayout = new VerticalLayout();

        mainLayout.addComponent(calendar);

        mainLayout.setMargin(true);

        setCaption(Messages.getMessage("com.alcidauk.courses.planning.default.session.modify.unavailable"));
        setContent(mainLayout);
    }
}
