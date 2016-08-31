package com.alcidauk.ui;

import com.alcidauk.data.repository.DefaultSessionRepository;
import com.alcidauk.ui.calendar.DefaultSessionsEventProvider;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        DefaultSessionsEventProvider calendarProvider = new DefaultSessionsEventProvider(defaultSessionRepository, calendar.getStartDate());
        calendar.setEventProvider(calendarProvider);

        calendar.addStyleName("calendar");

        mainLayout = new VerticalLayout();

        mainLayout.addComponent(calendar);

        mainLayout.setMargin(true);

        setCaption("Modifier le planning des disponibilités par défaut");
        setContent(mainLayout);
    }
}
