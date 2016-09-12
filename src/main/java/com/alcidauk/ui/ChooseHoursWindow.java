package com.alcidauk.ui;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.PlanningPeriodEventTypeRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alcidauk on 29/08/16.
 */
public class ChooseHoursWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(ChooseHoursWindow.class);


    private Instant startInstant;
    private Instant endInstant;

    private PlanningPeriodRepository planningPeriodRepository;
    private PlanningPeriodEventTypeRepository planningPeriodEventTypeRepository;
    private WorkSessionTypeRepository workSessionTypeRepository;

    private VerticalLayout mainLayout;

    public ChooseHoursWindow(Instant startInstant, Instant endInstant, PlanningPeriodRepository planningPeriodRepository,
                             PlanningPeriodEventTypeRepository planningPeriodEventTypeRepository,
                             WorkSessionTypeRepository workSessionTypeRepository) {
        this.startInstant = startInstant;
        this.endInstant = endInstant;
        this.planningPeriodRepository = planningPeriodRepository;
        this.planningPeriodEventTypeRepository = planningPeriodEventTypeRepository;
        this.workSessionTypeRepository = workSessionTypeRepository;
    }

    public void init(){
        mainLayout = new VerticalLayout();

        PlanningPeriod planningPeriod = planningPeriodRepository.findByStartInstantAndEndInstant(startInstant, endInstant);

        if(planningPeriod == null){
            log.error(String.format("Error retrieving planning for dates %s to %s.", startInstant.toString(), endInstant.toString()));
        } else {
            List<PlanningPeriodEventType> planningPeriodEventTypes = planningPeriod.getPlanningPeriodEventTypeList();
            if(planningPeriodEventTypes == null || planningPeriodEventTypes.isEmpty()){
                planningPeriodEventTypes = createPlanningPeriodEventTypesForPeriod(planningPeriod);
            }

            for (PlanningPeriodEventType planningPeriodEventType : planningPeriodEventTypes) {
                FormLayout parameterForm = new FormLayout();
                parameterForm.setCaption(StringUtils.capitalize(planningPeriodEventType.getType().getName()));

                TextField duration = new TextField("Nombre d'heures");
                duration.setValue(String.valueOf(planningPeriodEventType.getPeriodDuration().toHours()));

                parameterForm.addComponent(duration);

                mainLayout.addComponent(parameterForm);
            }
        }

        Button validButton = new Button("Valider");
        Button cancelButton = new Button("Annuler");

        HorizontalLayout buttonsLayout = new HorizontalLayout(validButton, cancelButton);
        buttonsLayout.setMargin(true);
        buttonsLayout.setWidth(100, Unit.PERCENTAGE);

        mainLayout.addComponent(buttonsLayout);

        mainLayout.setMargin(true);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dates = String.format("du %s au %s", dateTimeFormatter.format(startInstant.atZone(ZoneId.systemDefault())),
                dateTimeFormatter.format(endInstant.atZone(ZoneId.systemDefault())));

        setCaption(String.format("Paramètres %s", dates));
        setContent(mainLayout);
    }

    private List<PlanningPeriodEventType> createPlanningPeriodEventTypesForPeriod(PlanningPeriod planningPeriod) {
        log.info("Creating planningPeriodEventTypes for planningPeriod " + planningPeriod.toString());
        List<PlanningPeriodEventType> planningPeriodEventTypes = new ArrayList<>();

        List<WorkSessionType> workSessionTypes =  workSessionTypeRepository.findAll();
        for(WorkSessionType workSessionType : workSessionTypes){
            PlanningPeriodEventType planningPeriodEventType = new PlanningPeriodEventType(Duration.ZERO, workSessionType, planningPeriod);
            planningPeriodEventType = planningPeriodEventTypeRepository.save(planningPeriodEventType);

            planningPeriodEventTypes.add(planningPeriodEventType);
        }

        return planningPeriodEventTypes;
    }
}
