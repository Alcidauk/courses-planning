package com.alcidauk.ui.calendar;

import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.ui.CoursesUI;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alcidauk on 23/08/16.
 */
public class WorkSessionSettingsWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(WorkSessionSettingsWindow.class);

    private WorkSessionCalendarEventBean workSessionCalendarEventBean;

    private WorkSessionRepository workSessionRepository;

    private CheckBox doneCheck;
    private TextArea descriptionTxt;

    private FieldGroup calendarEventFieldGroup;

    public WorkSessionSettingsWindow(WorkSessionRepository workSessionRepository, WorkSessionCalendarEventBean workSessionCalendarEventBean) {
        this.workSessionCalendarEventBean = workSessionCalendarEventBean;
        this.workSessionRepository = workSessionRepository;
    }

    public void init(){
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);

        doneCheck = new CheckBox("Effectué");

        descriptionTxt = new TextArea("Description :");

        calendarEventFieldGroup = new BeanFieldGroup<>(WorkSession.class);
        calendarEventFieldGroup.bind(doneCheck, "done");
        calendarEventFieldGroup.bind(descriptionTxt, "description");
        calendarEventFieldGroup.setItemDataSource(new BeanItem<>(workSessionCalendarEventBean));

        doneCheck.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
                fireEventChanged();
                fireClose();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            workSessionRepository.save(workSessionCalendarEventBean.getWorkSession());
        });

        subContent.addComponent(doneCheck);

        setCaption(workSessionCalendarEventBean.getCaption());
        setContent(subContent);
    }

    private void fireEventChanged() {
        for(WorkSessionTypeListener listener :  ((CoursesUI) UI.getCurrent()).getWorkSessionTypeListeners()){
            listener.update(new WorkSessionTypeUpdatedEvent(this, workSessionCalendarEventBean.getWorkSession().getType()));
        }
    }
}
