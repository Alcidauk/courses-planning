package com.alcidauk.ui;

import com.alcidauk.data.bean.User;
import com.alcidauk.data.repository.UserRepository;
import com.alcidauk.login.AccessControl;
import com.alcidauk.login.BasicAccessControl;
import com.alcidauk.ui.calendar.CalendarCoursesEventTypeListener;
import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alcidauk on 20/08/16.
 */
@Theme("mytheme")
@SpringUI(path = "")
@Widgetset("com.alcidauk.MyAppWidgetset")
public class CoursesUI extends UI {

    private static final Logger log = LoggerFactory.getLogger(CoursesUI.class);

    private List<CalendarCoursesEventTypeListener> calendarCoursesEventTypeListeners;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccessControl accessControl;
    @Autowired
    private HomeLayout homeLayout;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        calendarCoursesEventTypeListeners = new ArrayList<>();

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

    public List<CalendarCoursesEventTypeListener> getCalendarCoursesEventTypeListeners() {
        return calendarCoursesEventTypeListeners;
    }

    public void addCalendarCoursesEventTypeListeners(CalendarCoursesEventTypeListener listener){
        calendarCoursesEventTypeListeners.add(listener);
    }
}