package com.alcidauk.ui.calendar.defaultsession.handlers;

import com.alcidauk.app.Messages;
import com.alcidauk.ui.calendar.defaultsession.DefaultSessionsEventProvider;
import com.alcidauk.ui.dto.DefaultSessionCalendarBean;
import com.vaadin.event.Action;
import com.vaadin.ui.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alcidauk on 06/09/16.
 */
public class DefaultSessionEventContextMenuHandler implements Action.Handler {

    private static final Logger log = LoggerFactory.getLogger(DefaultSessionEventContextMenuHandler.class);

    private List<Action> actions;

    public DefaultSessionEventContextMenuHandler() {
        actions = new ArrayList<>();
        actions.add(new Action(Messages.getMessage("com.alcidauk.courses.planning.default.session.remove")));
    }

    @Override
    public Action[] getActions(Object o, Object o1) {
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void handleAction(Action action, Object o, Object o1) {
        if(action.getCaption().equals(Messages.getMessage("com.alcidauk.courses.planning.default.session.remove"))){
            Calendar calendarHandler = (Calendar) o;

            DefaultSessionsEventProvider eventProvider = (DefaultSessionsEventProvider) calendarHandler.getEventProvider();
            eventProvider.removeSession((DefaultSessionCalendarBean) o1);

            calendarHandler.markAsDirty();
        }
    }
}
