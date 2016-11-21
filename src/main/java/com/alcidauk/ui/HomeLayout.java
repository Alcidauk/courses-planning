package com.alcidauk.ui;

import com.alcidauk.app.Messages;
import com.alcidauk.app.SessionGenerator;
import com.alcidauk.data.bean.PlanningPeriod;
import com.alcidauk.data.bean.PlanningPeriodEventType;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.*;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.calendar.defaultsession.DefaultSessionSettingsWindow;
import com.alcidauk.ui.calendar.worksession.ExternalWorkSessionChangeListener;
import com.alcidauk.ui.calendar.worksession.FromExternalWorkSessionUpdatedEvent;
import com.alcidauk.ui.calendar.worksession.WorkSessionCalendar;
import com.alcidauk.ui.calendar.worksession.WorkSessionTypeInPeriodLayout;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

/**
 * Created by alcidauk on 22/08/16.
 */
@SpringComponent
@UIScope
public class HomeLayout extends VerticalLayout {

    private HorizontalLayout topbar;
    private HorizontalLayout mainLayout;

    @Autowired
    private WorkSessionCalendar calendar;

    private VerticalLayout rightLayout;

    @Autowired
    private WorkSessionRepository workSessionRepository;

    @Autowired
    private WorkSessionTypeRepository workSessionTypeRepository;

    @Autowired
    private PlanningPeriodRepository planningPeriodRepository;

    @Autowired
    private PlanningPeriodEventTypeRepository planningPeriodEventTypeRepository;

    @Autowired
    private DefaultUnavailabilitySessionRepository defaultUnavailabilitySessionRepository;

    protected void init() {
        this.createTopbar();
        this.createMainLayout();
        this.addMainComponents();
    }

    private void createMainLayout() {
        this.createCalendar();
        this.createRightLayout();

        this.mainLayout = new HorizontalLayout(calendar, rightLayout);
        this.mainLayout.setSizeFull();

        this.mainLayout.setExpandRatio(calendar, 4);
        this.mainLayout.setExpandRatio(rightLayout, 1);
    }

    private void createRightLayout() {
        rightLayout = new VerticalLayout();

        List<WorkSessionType> workSessionTypes = workSessionTypeRepository.findNotSystem();
        for(WorkSessionType workSessionType : workSessionTypes){
            WorkSessionTypeInPeriodLayout workSessionTypeInPeriodLayout =
                    new WorkSessionTypeInPeriodLayout(workSessionRepository, workSessionType);
            workSessionTypeInPeriodLayout.init();

            workSessionTypeInPeriodLayout.setWidth(100, Unit.PERCENTAGE);
            workSessionTypeInPeriodLayout.addStyleName("grey-background");

            rightLayout.addComponent(workSessionTypeInPeriodLayout);
        }

        Button chooseHoursButton = new Button(Messages.getMessage("com.alcidauk.courses.planning.choose.hours"));
        chooseHoursButton.addClickListener((Button.ClickListener) clickEvent -> {
            ChooseHoursWindow window = new ChooseHoursWindow(Instant.ofEpochMilli(calendar.getStartDate().getTime()),
                    Instant.ofEpochMilli(calendar.getEndDate().getTime()), planningPeriodRepository,
                    planningPeriodEventTypeRepository, workSessionTypeRepository);
            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        });
        chooseHoursButton.setWidth(100, Unit.PERCENTAGE);

        Button generateSessionsButton = new Button(Messages.getMessage("com.alcidauk.courses.planning.generate.sessions.for.period"));
        generateSessionsButton.addClickListener((Button.ClickListener) clickEvent -> {
            PlanningPeriod currentPlanningPeriod = getCurrentPlanningPeriod();
            List<WorkSession> workSessions = new SessionGenerator(
                    currentPlanningPeriod,
                    getUnavailableWorkSessions(),
                    getPlanningPeriodEventTypes(),
                    workSessionRepository).generateSessions();
            workSessionRepository.save(workSessions);
            fireExternalWorkSessionEventChanged(currentPlanningPeriod.getStartInstant(), currentPlanningPeriod.getEndInstant());
        });
        generateSessionsButton.setWidth(100, Unit.PERCENTAGE);

        rightLayout.addComponent(chooseHoursButton);
        rightLayout.addComponent(generateSessionsButton);
        rightLayout.setMargin(true);
    }

    private PlanningPeriod getCurrentPlanningPeriod(){
        Instant startInstant = Instant.ofEpochMilli(calendar.getStartDate().getTime());
        Instant endInstant = Instant.ofEpochMilli(calendar.getEndDate().getTime());
        return planningPeriodRepository.findByStartInstantAndEndInstantAndUser(startInstant, endInstant, CurrentUser.get());
    }

    private List<WorkSession> getUnavailableWorkSessions(){
        Instant startInstant = Instant.ofEpochMilli(calendar.getStartDate().getTime());
        Instant endInstant = Instant.ofEpochMilli(calendar.getEndDate().getTime());

        return workSessionRepository.findByTypeBetweenStartInstantAndEndInstant(
                workSessionTypeRepository.findByName("unavailable"), startInstant, endInstant);
    }

    private List<PlanningPeriodEventType> getPlanningPeriodEventTypes(){
        return planningPeriodEventTypeRepository.findByNotSystemTypeAndPeriod(getCurrentPlanningPeriod());
    }

    private void createCalendar() {
        calendar.init();
    }

    private void createTopbar(){
        Label nameLabel = new Label(Messages.getMessage("com.alcidauk.courses.planning.hello", CurrentUser.get().getUsername()));
        nameLabel.setHeight(100, Unit.PERCENTAGE);
        nameLabel.addStyleName("white-font");
        nameLabel.addStyleName("margin-5");

        MenuBar.Command defaultUnavailabilitySessionCommand = (MenuBar.Command) selectedItem -> {
            DefaultSessionSettingsWindow window = new DefaultSessionSettingsWindow(defaultUnavailabilitySessionRepository);

            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        };
        MenuBar.Command manageWorkSessionTypesCommand = (MenuBar.Command) selectedItem -> {
            ManageWorkSessionTypesWindow window = new ManageWorkSessionTypesWindow(workSessionTypeRepository, workSessionRepository);

            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        };
        MenuBar.Command logoutCommand = (MenuBar.Command) menuItem -> logout();

        MenuBar buttonsMenu = new MenuBar();
        buttonsMenu.addStyleName("margin-5");

        MenuBar.MenuItem settingsItem = buttonsMenu.addItem(Messages.getMessage("com.alcidauk.courses.planning.settings"), null, null);
        settingsItem.addItem(Messages.getMessage("com.alcidauk.courses.planning.modify.default.unavailable.sessions"), defaultUnavailabilitySessionCommand);
        settingsItem.addItem(Messages.getMessage("com.alcidauk.courses.planning.manage.work.session.types"), manageWorkSessionTypesCommand);

        MenuBar.MenuItem logoutItem = buttonsMenu.addItem(Messages.getMessage("com.alcidauk.courses.planning.logout"), null, logoutCommand);
        logoutItem.setStyleName("red-background");

        this.topbar = new HorizontalLayout(nameLabel, buttonsMenu);
        this.topbar.addStyleName("topbar");
        this.topbar.setHeight(50, Unit.PIXELS);
        this.topbar.setWidth(100, Unit.PERCENTAGE);

        this.topbar.setExpandRatio(nameLabel, 1);
        this.topbar.setComponentAlignment(buttonsMenu, Alignment.MIDDLE_LEFT);
    }

    private void addMainComponents(){
        this.addComponent(this.topbar);
        this.addComponent(this.mainLayout);

        this.setExpandRatio(this.mainLayout, 1);
        this.setSizeFull();
    }

    private void logout() {
        CurrentUser.set(null);
        getUI().getSession().close();
        getUI().getPage().reload();
    }

    private void fireExternalWorkSessionEventChanged(Instant startInstant, Instant endInstant) {
        for(ExternalWorkSessionChangeListener listener :  ((CoursesUI) UI.getCurrent()).getExternalWorkSessionChangeListeners()){
            listener.update(new FromExternalWorkSessionUpdatedEvent(this, startInstant, endInstant));
        }
    }
}
