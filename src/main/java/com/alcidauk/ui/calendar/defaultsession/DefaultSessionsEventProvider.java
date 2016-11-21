package com.alcidauk.ui.calendar.defaultsession;

import com.alcidauk.data.bean.DefaultUnavailabilitySession;
import com.alcidauk.data.repository.DefaultUnavailabilitySessionRepository;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.CoursesUI;
import com.alcidauk.ui.calendar.worksession.ExternalWorkSessionChangeListener;
import com.alcidauk.ui.calendar.worksession.FromExternalWorkSessionUpdatedEvent;
import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alcidauk on 30/08/16.
 */
public class DefaultSessionsEventProvider implements CalendarEventProvider {

    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;
    private Date startDate;

    private Calendar calendar;

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionsEventProvider.class);

    public DefaultSessionsEventProvider(DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository, Date startDate, Calendar calendar) {
        this.defaultUnavailabilitySessionRepository = defaultUnavailabilitySessionRepository;
        this.startDate = startDate;
        this.calendar = calendar;
    }

    @Override
    public List<CalendarEvent> getEvents(Date start, Date end) {
        List<DefaultUnavailabilitySession> sessions = defaultUnavailabilitySessionRepository.findByUser(CurrentUser.get());
        return sessions.stream().map(session ->
                new DefaultSessionCalendarBean(session, startDate)).collect(Collectors.toList());
    }

    public void updateSessionBean(DefaultSessionCalendarBean calendarBean){
        calendarBean.updateDefaultSession();
        defaultUnavailabilitySessionRepository.save(calendarBean.getDefaultUnavailabilitySession());

        fireExternalWorkSessionEventChanged(calendarBean);
    }

    public void createSessionBean(DefaultSessionCalendarBean calendarBean){
        calendarBean.createDefaultSession();
        defaultUnavailabilitySessionRepository.save(calendarBean.getDefaultUnavailabilitySession());

        fireExternalWorkSessionEventChanged(calendarBean);
    }

    public void removeSession(DefaultSessionCalendarBean calendarBean) {
        defaultUnavailabilitySessionRepository.delete(calendarBean.getDefaultUnavailabilitySession());
        fireExternalWorkSessionEventChanged(calendarBean);
    }

    private void fireExternalWorkSessionEventChanged(Instant startInstant, Instant endInstant) {
        for(ExternalWorkSessionChangeListener listener :  ((CoursesUI) UI.getCurrent()).getExternalWorkSessionChangeListeners()){
            listener.update(new FromExternalWorkSessionUpdatedEvent(calendar, startInstant, endInstant));
        }
    }

    private void fireExternalWorkSessionEventChanged(DefaultSessionCalendarBean calendarBean) {
        fireExternalWorkSessionEventChanged(calendarBean.getStart().toInstant(), calendarBean.getEnd().toInstant());
    }
}
