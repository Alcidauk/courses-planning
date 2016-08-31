package com.alcidauk.ui;

import com.alcidauk.data.repository.UserRepository;
import com.alcidauk.login.AccessControl;
import com.alcidauk.ui.calendar.WorkSessionTypeListener;
import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by alcidauk on 20/08/16.
 */
@Theme("mytheme")
@SpringUI(path = "")
@Widgetset("com.alcidauk.MyAppWidgetset")
public class CoursesUI extends UI {

    private static final Logger log = LoggerFactory.getLogger(CoursesUI.class);

    private List<WorkSessionTypeListener> workSessionTypeListeners;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessControl accessControl;
    @Autowired
    private HomeLayout homeLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        UI.getCurrent().setLocale(Locale.FRANCE);

        workSessionTypeListeners = new ArrayList<>();

        if (!accessControl.isUserSignedIn()) {
            DefaultVerticalLoginForm loginForm = new DefaultVerticalLoginForm();
            loginForm.addLoginListener((LoginForm.LoginListener) loginEvent -> {
                if(accessControl.signIn(loginEvent.getUserName(), loginEvent.getPassword())){
                    showMainView();
                } else {
                    System.err.println(
                            "Logged in with user name " + loginEvent.getUserName() +
                                    " and password of length " + loginEvent.getPassword());
                }
            });

            setContent(loginForm);
        } else {
            showMainView();
        }
    }

    protected void showMainView() {
        homeLayout.init();
        setContent(homeLayout);

        this.addStyleName("backgroundimage");

        //getNavigator().navigateTo(getNavigator().getState());
    }

    public List<WorkSessionTypeListener> getWorkSessionTypeListeners() {
        return workSessionTypeListeners;
    }

    public void addCalendarCoursesEventTypeListeners(WorkSessionTypeListener listener){
        workSessionTypeListeners.add(listener);
    }
}