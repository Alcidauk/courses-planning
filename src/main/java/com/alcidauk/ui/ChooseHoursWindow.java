package com.alcidauk.ui;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.PlanningPeriodEventTypeRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.dto.PlanningPeriodEventTypeBean;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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

        PlanningPeriod planningPeriod = planningPeriodRepository.findByStartInstantAndEndInstantAndUser(startInstant, endInstant, CurrentUser.get());

        if(planningPeriod == null){
            log.error(String.format("Error retrieving planning for dates %s to %s.", startInstant.toString(), endInstant.toString()));
        } else {
            List<PlanningPeriodEventType> planningPeriodEventTypes = planningPeriodEventTypeRepository.findByNotSystemTypeAndPeriod(planningPeriod);

            if(planningPeriodEventTypes == null || planningPeriodEventTypes.size() < workSessionTypeRepository.countNotSystem()){
                planningPeriodEventTypes = getOrCreatePlanningPeriodEventTypesForPeriod(planningPeriod);
            }

            BeanItemContainer<PlanningPeriodEventTypeBean> beanItemContainer = new BeanItemContainer<>(PlanningPeriodEventTypeBean.class);
            for(PlanningPeriodEventType planningPeriodEventType : planningPeriodEventTypes) {
                beanItemContainer.addBean(new PlanningPeriodEventTypeBean(planningPeriodEventType));
            }

            Grid planningPeriodEventTypesGrid = new Grid(beanItemContainer);

            planningPeriodEventTypesGrid.setEditorEnabled(true);
            planningPeriodEventTypesGrid.setColumns("typeName", "durationHours");

            planningPeriodEventTypesGrid.getColumn("typeName").setHeaderCaption(Messages.getMessage("com.alcidauk.courses.planning.work.session.type"));
            planningPeriodEventTypesGrid.getColumn("typeName").setEditable(false);

            planningPeriodEventTypesGrid.getColumn("durationHours").setHeaderCaption(Messages.getMessage("com.alcidauk.courses.planning.choose.hours.number"));
            planningPeriodEventTypesGrid.getColumn("durationHours").setEditable(true);

            planningPeriodEventTypesGrid.setImmediate(true);
            planningPeriodEventTypesGrid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
                @Override
                public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

                }

                @Override
                public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                    BeanItem<PlanningPeriodEventTypeBean> planningPeriodEventTypeBeanItem =
                            (BeanItem<PlanningPeriodEventTypeBean>) commitEvent.getFieldBinder().getItemDataSource();
                    planningPeriodEventTypeRepository.save(planningPeriodEventTypeBeanItem.getBean().getPlanningPeriodEventType());
                }
            });

            planningPeriodEventTypesGrid.setEditorCancelCaption(Messages.getMessage("com.alcidauk.courses.planning.choose.hours.cancel"));
            planningPeriodEventTypesGrid.setEditorSaveCaption(Messages.getMessage("com.alcidauk.courses.planning.choose.hours.save"));

            mainLayout.addComponent(planningPeriodEventTypesGrid);
        }

        mainLayout.setMargin(true);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dates = Messages.getMessage("com.alcidauk.courses.planning.choose.hours.from.to",
                dateTimeFormatter.format(startInstant.atZone(ZoneId.systemDefault())),
                dateTimeFormatter.format(endInstant.atZone(ZoneId.systemDefault()))
        );

        setCaption(Messages.getMessage("com.alcidauk.courses.planning.choose.hours.settings.of", dates));
        setContent(mainLayout);
    }

    private List<PlanningPeriodEventType> getOrCreatePlanningPeriodEventTypesForPeriod(PlanningPeriod planningPeriod) {
        List<PlanningPeriodEventType> newPlanningPeriodEventTypes = new ArrayList<>();

        List<WorkSessionType> workSessionTypes =  workSessionTypeRepository.findNotSystem();
        for(WorkSessionType workSessionType : workSessionTypes){
            List<PlanningPeriodEventType> planningPeriodEventTypesForTypeAndPeriod =
                    planningPeriodEventTypeRepository.findByTypeAndPeriod(workSessionType, planningPeriod);
            PlanningPeriodEventType planningPeriodEventType;
            if(planningPeriodEventTypesForTypeAndPeriod == null || planningPeriodEventTypesForTypeAndPeriod.isEmpty()) {
                log.info(String.format("Creating planningPeriodEventTypes for planningPeriod %s and %s",
                        planningPeriod.getStartInstant(), workSessionType.getName()));

                planningPeriodEventType = new PlanningPeriodEventType(Duration.ZERO, workSessionType, planningPeriod);
                planningPeriodEventType = planningPeriodEventTypeRepository.save(planningPeriodEventType);
            } else {
                planningPeriodEventType = planningPeriodEventTypesForTypeAndPeriod.get(0);
            }

            newPlanningPeriodEventTypes.add(planningPeriodEventType);
        }

        return newPlanningPeriodEventTypes;
    }
}
