package com.alcidauk.ui.calendar.defaultsession;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.data.repository.DefaultUnavailabilitySessionRepository;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 30/08/16.
 */
public class DefaultSessionsEventProvider implements CalendarEventProvider {

    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;
    private Date startDate;

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionsEventProvider.class);

    public DefaultSessionsEventProvider(DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository, Date startDate) {
        this.defaultUnavailabilitySessionRepository = defaultUnavailabilitySessionRepository;
        this.startDate = startDate;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        List<DefaultUnavailabilitySession> sessions = defaultUnavailabilitySessionRepository.findByUsername(CurrentUser.get());
        return sessions.stream().map(session ->
                new DefaultSessionCalendarBean(session, startDate)).collect(Collectors.toList());
    }

    public void updateSessionBean(DefaultSessionCalendarBean calendarBean){
        calendarBean.updateDefaultSession();
        defaultUnavailabilitySessionRepository.save(calendarBean.getDefaultUnavailabilitySession());
    }
}
