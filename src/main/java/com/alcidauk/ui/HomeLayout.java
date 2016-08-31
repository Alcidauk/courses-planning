package com.alcidauk.ui;

import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.alcidauk.data.repository.DefaultSessionRepository;
import com.alcidauk.data.repository.PlanningPeriodRepository;
import com.alcidauk.login.CurrentUser;
import com.alcidauk.ui.calendar.CalendarCoursesEventProvider;
import com.alcidauk.ui.calendar.CalendarDetailWindow;
import com.alcidauk.ui.calendar.CalendarTypeLayout;
import com.alcidauk.ui.dto.CalendarCoursesEventBean;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * Created by alcidauk on 22/08/16.
 */
@SpringComponent
@UIScope
public class HomeLayout extends VerticalLayout {

    private HorizontalLayout topbar;
    private HorizontalLayout mainLayout;

    private Calendar calendar;
    private VerticalLayout rightLayout;

    @Autowired
    private WorkSessionRepository workSessionRepository;

    @Autowired
    private WorkSessionTypeRepository workSessionTypeRepository;

    @Autowired
    private PlanningPeriodRepository planningPeriodRepository;

    @Autowired
    private DefaultSessionRepository defaultSessionRepository;

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
            CalendarTypeLayout calendarTypeLayout = new CalendarTypeLayout(workSessionRepository, workSessionType);
            calendarTypeLayout.init();

            calendarTypeLayout.setWidth(100, Unit.PERCENTAGE);
            calendarTypeLayout.addStyleName("grey-background");

            rightLayout.addComponent(calendarTypeLayout);
        }

        Button chooseHoursButton = new Button("Choisir les nombres d'heures");
        chooseHoursButton.addClickListener((Button.ClickListener) clickEvent -> {
            ChooseHoursWindow window = new ChooseHoursWindow(Instant.ofEpochMilli(calendar.getStartDate().getTime()),
                    Instant.ofEpochMilli(calendar.getEndDate().getTime()),
                    planningPeriodRepository);

            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        });
        chooseHoursButton.setWidth(100, Unit.PERCENTAGE);

        rightLayout.addComponent(chooseHoursButton);
        rightLayout.setMargin(true);
    }

    private void createCalendar() {
        calendar = new Calendar();
        calendar.setLocale(Locale.FRENCH);
        calendar.setFirstVisibleHourOfDay(6);
        calendar.setLastVisibleHourOfDay(21);

        calendar.setHandler(new CalendarComponentEvents.EventClickHandler() {
            public void eventClick(CalendarComponentEvents.EventClick event) {
                CalendarDetailWindow calendarDetailWindow =
                        new CalendarDetailWindow(workSessionRepository, (CalendarCoursesEventBean) event.getCalendarEvent());
                calendarDetailWindow.init();

                UI.getCurrent().addWindow(calendarDetailWindow);
                calendarDetailWindow.center();
            }
        });

        calendar.setEventProvider(new CalendarCoursesEventProvider(workSessionRepository));

        calendar.setHeight(100, Unit.PERCENTAGE);
        calendar.addStyleName("calendar");
    }

    private void createTopbar(){
        Label nameLabel = new Label("Coucou, " + CurrentUser.get() + " !");
        nameLabel.addStyleName("white-font");
        nameLabel.addStyleName("margin-5");

        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.addStyleName("margin-5");

        logoutBtn.setWidth(150, Unit.PIXELS);
        logoutBtn.addClickListener(this::logout);

        Button periodPlanning = new Button("Modifier les disponibilités par défaut");
        periodPlanning.addClickListener((Button.ClickListener) clickEvent -> {
            PeriodWindow window = new PeriodWindow(defaultSessionRepository);

            window.init();

            UI.getCurrent().addWindow(window);
            window.center();
        });
        periodPlanning.setWidth(250, Unit.PIXELS);

        this.topbar = new HorizontalLayout(nameLabel, periodPlanning, logoutBtn);
        this.topbar.addStyleName("topbar");
        this.topbar.setHeight(50, Unit.PIXELS);
        this.topbar.setWidth(100, Unit.PERCENTAGE);

        this.topbar.setExpandRatio(nameLabel, 1);
        this.topbar.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
        this.topbar.setComponentAlignment(logoutBtn, Alignment.MIDDLE_LEFT);
    }

    private void addMainComponents(){
        this.addComponent(this.topbar);
        this.addComponent(this.mainLayout);

        this.setExpandRatio(this.mainLayout, 1);
        this.setSizeFull();
    }

    private void logout(Button.ClickEvent clickEvent) {
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
