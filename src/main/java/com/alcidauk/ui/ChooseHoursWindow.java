package com.alcidauk.ui;

import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.vaadin.ui.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by alcidauk on 29/08/16.
 */
public class ChooseHoursWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(ChooseHoursWindow.class);


    private Instant startInstant;
    private Instant endInstant;

    private PlanningPeriodRepository planningPeriodRepository;

    private VerticalLayout mainLayout;

    public ChooseHoursWindow(Instant startDate, Instant endDate, PlanningPeriodRepository planningPeriodRepository) {
        this.startInstant = startDate;
        this.endInstant = endDate;
        this.planningPeriodRepository = planningPeriodRepository;
    }

    public void init(){
        mainLayout = new VerticalLayout();

        List<PlanningPeriod> planningPeriods = planningPeriodRepository.findByStartInstantAndEndInstant(startInstant, endInstant);

        if(planningPeriods == null || planningPeriods.isEmpty()){
            log.error(String.format("Error retrieving planning for dates %s to %s.", startInstant.toString(), endInstant.toString()));
        } else {
            for (PlanningPeriod planningPeriod : planningPeriods) {
                FormLayout parameterForm = new FormLayout();
                parameterForm.setCaption(StringUtils.capitalize(planningPeriod.getType().getName()));

                TextField duration = new TextField("Nombre d'heures");
                duration.setValue(String.valueOf(planningPeriod.getPeriodDuration().toHours()));

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
}
