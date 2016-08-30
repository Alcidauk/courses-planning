package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.CalendarCoursesEvent;
import com.alcidauk.data.repository.CalendarCoursesEventRepository;
import com.alcidauk.ui.CoursesUI;
import com.alcidauk.ui.dto.CalendarCoursesEventBean;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by alcidauk on 23/08/16.
 */
public class CalendarDetailWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(CalendarDetailWindow.class);

    private CalendarCoursesEventBean calendarCoursesEventBean;

    private CalendarCoursesEventRepository coursesEventRepository;

    private CheckBox doneCheck;
    private TextArea descriptionTxt;

    private FieldGroup calendarEventFieldGroup;

    public CalendarDetailWindow(CalendarCoursesEventRepository coursesEventRepository, CalendarCoursesEventBean calendarCoursesEventBean) {
        this.calendarCoursesEventBean = calendarCoursesEventBean;
        this.coursesEventRepository = coursesEventRepository;
    }

    public void init(){
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);

        doneCheck = new CheckBox("Effectué");

        descriptionTxt = new TextArea("Description :");

        calendarEventFieldGroup = new BeanFieldGroup<>(CalendarCoursesEvent.class);
        calendarEventFieldGroup.bind(doneCheck, "done");
        calendarEventFieldGroup.bind(descriptionTxt, "description");
        calendarEventFieldGroup.setItemDataSource(new BeanItem<>(calendarCoursesEventBean));

        doneCheck.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
                fireEventChanged();
                fireClose();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            coursesEventRepository.save(calendarCoursesEventBean.getCalendarCoursesEvent());
        });

        subContent.addComponent(doneCheck);

        setCaption(calendarCoursesEventBean.getCaption());
        setContent(subContent);
    }

    private void fireEventChanged() {
        for(CalendarCoursesEventTypeListener listener :  ((CoursesUI) UI.getCurrent()).getCalendarCoursesEventTypeListeners()){
            listener.update(new CalendarCoursesEventTypeUpdatedEvent(this, calendarCoursesEventBean.getCalendarCoursesEvent().getType()));
        }
    }
}
