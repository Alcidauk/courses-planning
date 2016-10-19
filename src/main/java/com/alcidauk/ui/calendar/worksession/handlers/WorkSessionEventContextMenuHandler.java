package com.alcidauk.ui.calendar.worksession.handlers;

import com.alcidauk.app.Messages;
import com.alcidauk.ui.calendar.worksession.WorkSessionCalendarEventProvider;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.event.Action;
import com.vaadin.ui.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alcidauk on 06/09/16.
 */
public class WorkSessionEventContextMenuHandler implements Action.Handler {

    private static final Logger log = LoggerFactory.getLogger(WorkSessionEventContextMenuHandler.class);

    private List<Action> actions;

    public WorkSessionEventContextMenuHandler() {
        actions = new ArrayList<>();
        actions.add(new Action(Messages.getMessage("com.alcidauk.courses.planning.work.session.remove")));
    }

    @Override
    public Action[] getActions(Object o, Object o1) {
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void handleAction(Action action, Object o, Object o1) {
        if(action.getCaption().equals(Messages.getMessage("com.alcidauk.courses.planning.work.session.remove"))){
            Calendar calendarHandler = (Calendar) o;

            WorkSessionCalendarEventProvider eventProvider = (WorkSessionCalendarEventProvider) calendarHandler.getEventProvider();
            eventProvider.removeSession((WorkSessionCalendarEventBean) o1);

            calendarHandler.markAsDirty();
        }
    }
}
