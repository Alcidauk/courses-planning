package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.data.bean.PlanningPeriod;
import com.vaadin.ui.Component;

/**
 * Created by alcidauk on 24/08/16.
 */
public class ShowPlanningPeriodChangedEvent extends Component.Event{

    private PlanningPeriod planningPeriod;

    public ShowPlanningPeriodChangedEvent(Component source, PlanningPeriod planningPeriod) {
        super(source);
        this.planningPeriod = planningPeriod;
    }

    public PlanningPeriod getPlanningPeriod() {
        return planningPeriod;
    }
}
