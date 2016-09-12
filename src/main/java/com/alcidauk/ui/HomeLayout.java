package com.alcidauk.ui;

import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.*;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.calendar.defaultsession.DefaultSessionSettingsWindow;
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

        List<WorkSessionType> workSessionTypes = workSessionTypeRepository.findAll();
        for(WorkSessionType workSessionType : workSessionTypes){
            WorkSessionTypeInPeriodLayout workSessionTypeInPeriodLayout =
                    new WorkSessionTypeInPeriodLayout(workSessionRepository, workSessionType);
            workSessionTypeInPeriodLayout.init();

            workSessionTypeInPeriodLayout.setWidth(100, Unit.PERCENTAGE);
            workSessionTypeInPeriodLayout.addStyleName("grey-background");

            rightLayout.addComponent(workSessionTypeInPeriodLayout);
        }

        Button chooseHoursButton = new Button("Choisir les nombres d'heures");
        chooseHoursButton.addClickListener((Button.ClickListener) clickEvent -> {
            ChooseHoursWindow window = new ChooseHoursWindow(Instant.ofEpochMilli(calendar.getStartDate().getTime()),
                    Instant.ofEpochMilli(calendar.getEndDate().getTime()), planningPeriodRepository,
                    planningPeriodEventTypeRepository, workSessionTypeRepository);
            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        });
        chooseHoursButton.setWidth(100, Unit.PERCENTAGE);

        rightLayout.addComponent(chooseHoursButton);
        rightLayout.setMargin(true);
    }

    private void createCalendar() {
        calendar.init();
    }

    private void createTopbar(){
        Label nameLabel = new Label("Coucou, " + CurrentUser.get() + " !");
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
            ManageWorkSessionTypesWindow window = new ManageWorkSessionTypesWindow(workSessionTypeRepository);

            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        };
        MenuBar.Command logoutCommand = (MenuBar.Command) menuItem -> logout();

        MenuBar buttonsMenu = new MenuBar();
        buttonsMenu.addStyleName("margin-5");

        MenuBar.MenuItem settingsItem = buttonsMenu.addItem("Paramètres", null, null);
        settingsItem.addItem("Modifier les indisponibilités par défaut", defaultUnavailabilitySessionCommand);
        settingsItem.addItem("Gérer les matières", manageWorkSessionTypesCommand);

        MenuBar.MenuItem logoutItem = buttonsMenu.addItem("Déconnexion", null, logoutCommand);
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

/*    protected void init() {
        saveButton.addClickListener(this::save);
    }

    void showChangesSaved() { // UI update method
        // Show a notification, make a label visible, etc.
    }

    void save(Button.ClickEvent event) { // UI logic method
        backend.save();
        showChangesSaved();
    }*/
}
