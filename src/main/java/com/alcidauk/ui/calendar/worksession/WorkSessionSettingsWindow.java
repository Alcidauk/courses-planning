package com.alcidauk.ui.calendar.worksession;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSession;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.alcidauk.ui.CoursesUI;
import com.alcidauk.ui.dto.WorkSessionCalendarEventBean;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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
    private WorkSessionTypeRepository workSessionTypeRepository;

    private CheckBox doneCheck;
    private TextField titleField;
    private TextArea descriptionTxt;
    private ComboBox sessionTypeCombo;

    private FieldGroup calendarEventFieldGroup;

    public WorkSessionSettingsWindow(WorkSessionCalendarEventBean workSessionCalendarEventBean,
                                     WorkSessionRepository workSessionRepository, WorkSessionTypeRepository workSessionTypeRepository) {
        this.workSessionCalendarEventBean = workSessionCalendarEventBean;
        this.workSessionRepository = workSessionRepository;
        this.workSessionTypeRepository = workSessionTypeRepository;
    }

    public void init(){
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);

        doneCheck = new CheckBox(Messages.getMessage("com.alcidauk.courses.planning.work.session.done"));

        descriptionTxt = new TextArea(Messages.getMessage("com.alcidauk.courses.planning.work.session.description") + " :");
        descriptionTxt.setNullRepresentation("");
        titleField = new TextField(Messages.getMessage("com.alcidauk.courses.planning.work.session.title") + " :");
        titleField.setNullRepresentation("");

        BeanItemContainer<WorkSessionType> notSystemSessionTypes = new BeanItemContainer<>(WorkSessionType.class,
                workSessionTypeRepository.findNotSystem());

        sessionTypeCombo = new ComboBox(Messages.getMessage("com.alcidauk.courses.planning.work.session.title") + " :", notSystemSessionTypes);
        sessionTypeCombo.setNullSelectionAllowed(false);
        sessionTypeCombo.setItemCaptionPropertyId("i18Name");

        calendarEventFieldGroup = new BeanFieldGroup<>(WorkSession.class);
        calendarEventFieldGroup.bind(titleField, "caption");
        calendarEventFieldGroup.bind(descriptionTxt, "description");
        calendarEventFieldGroup.bind(doneCheck, "done");
        calendarEventFieldGroup.bind(sessionTypeCombo, "type");
        calendarEventFieldGroup.setItemDataSource(new BeanItem<>(workSessionCalendarEventBean));

        titleField.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
                fireExternalWorkSessionEventChanged();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            workSessionRepository.save(workSessionCalendarEventBean.getWorkSession());
        });

        descriptionTxt.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            workSessionRepository.save(workSessionCalendarEventBean.getWorkSession());
        });

        doneCheck.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
                fireWorkSessionTypeCountEventChanged();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            workSessionRepository.save(workSessionCalendarEventBean.getWorkSession());
        });

        sessionTypeCombo.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            try {
                calendarEventFieldGroup.commit();
                fireWorkSessionTypeCountEventChanged();
                fireExternalWorkSessionEventChanged();
                fireClose();
            } catch (FieldGroup.CommitException e) {
                Notification.show("error");
            }
            workSessionRepository.save(workSessionCalendarEventBean.getWorkSession());
        });

        subContent.addComponent(titleField);
        subContent.addComponent(descriptionTxt);
        subContent.addComponent(sessionTypeCombo);
        subContent.addComponent(doneCheck);

        setCaption(workSessionCalendarEventBean.getCaption());

        setContent(subContent);
    }

    private void fireWorkSessionTypeCountEventChanged() {
        for(WorkSessionTypeListener listener :  ((CoursesUI) UI.getCurrent()).getWorkSessionTypeListeners()){
            listener.update(new WorkSessionTypeUpdatedEvent(this, workSessionCalendarEventBean.getWorkSession().getType()));
        }
    }

    private void fireExternalWorkSessionEventChanged() {
        for(ExternalWorkSessionChangeListener listener :  ((CoursesUI) UI.getCurrent()).getExternalWorkSessionChangeListeners()){
            listener.update(new FromExternalWorkSessionUpdatedEvent(this,
                    workSessionCalendarEventBean.getWorkSession().getStartInstant(),
                    workSessionCalendarEventBean.getWorkSession().getEndInstant()));
        }
    }
}
