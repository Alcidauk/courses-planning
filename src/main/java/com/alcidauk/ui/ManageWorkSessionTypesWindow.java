package com.alcidauk.ui;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionRepository;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import java.util.Locale;

/**
 * Created by alcidauk on 08/09/16.
 */
public class ManageWorkSessionTypesWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(ManageWorkSessionTypesWindow.class);

    private WorkSessionTypeRepository workSessionTypeRepository;
    private WorkSessionRepository workSessionRepository;

    public ManageWorkSessionTypesWindow(WorkSessionTypeRepository workSessionTypeRepository, WorkSessionRepository workSessionRepository) {
        this.workSessionTypeRepository = workSessionTypeRepository;
        this.workSessionRepository = workSessionRepository;
    }

    public void init() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);

        Grid workSessionTypesGrid = new Grid(Messages.getMessage("com.alcidauk.courses.planning.work.session.types"));

        BeanItemContainer<WorkSessionType> beanItemContainer = new BeanItemContainer<>(WorkSessionType.class);
        beanItemContainer.addAll(workSessionTypeRepository.findNotSystem());

        workSessionTypesGrid.setContainerDataSource(beanItemContainer);
        workSessionTypesGrid.setEditorEnabled(true);
        workSessionTypesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        workSessionTypesGrid.setColumns("name");
        workSessionTypesGrid.getColumn("name").setHeaderCaption("Nom");
        workSessionTypesGrid.getColumn("name").setEditable(true);
        workSessionTypesGrid.getColumn("name").setConverter(new Converter<String, String>() {
                    @Override
                    public String convertToModel(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
                        return s;
                    }

                    @Override
                    public String convertToPresentation(String s, Class<? extends String> aClass, Locale locale) throws ConversionException {
                        return Messages.getWorkSessionTypeNameMessage(s);
                    }

                    @Override
                    public Class<String> getModelType() {
                        return String.class;
                    }

                    @Override
                    public Class<String> getPresentationType() {
                        return String.class;
                    }
                }
        );
        workSessionTypesGrid.setImmediate(true);

        workSessionTypesGrid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {

            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                BeanItem<WorkSessionType> workSessionTypeBean = (BeanItem<WorkSessionType>) commitEvent.getFieldBinder().getItemDataSource();
                workSessionTypeRepository.save(workSessionTypeBean.getBean());
            }
        });

        Button newWorkSessionTypeButton = new Button(Messages.getMessage("com.alcidauk.courses.planning.add.work.session.type"));
        newWorkSessionTypeButton.addStyleName("margin-5");
        newWorkSessionTypeButton.addClickListener((Button.ClickListener) clickEvent -> {
            WorkSessionType workSessionType = new WorkSessionType(Messages.getMessage("com.alcidauk.courses.planning.new.work.session.type"), false);
            workSessionTypeRepository.save(workSessionType);
            beanItemContainer.addItem(workSessionType);
        });

        Button removeSelectedSessionTypeButton = new Button(Messages.getMessage("com.alcidauk.courses.planning.remove.selected.work.session.type"));
        removeSelectedSessionTypeButton.addStyleName("margin-5");
        removeSelectedSessionTypeButton.addClickListener((Button.ClickListener) clickEvent -> {
            Object selectedRow = workSessionTypesGrid.getSelectedRow();
            if(selectedRow != null){
                BeanItem<WorkSessionType> workSessionTypeBeanItem =
                        (BeanItem<WorkSessionType>) workSessionTypesGrid.getContainerDataSource().getItem(selectedRow);

                if(workSessionTypeBeanItem.getBean().getWorkSessions().size() > 0){
                    log.info("need to open removal window");
                    ConfirmDialog.show(UI.getCurrent(),
                            Messages.getMessage("com.alcidauk.courses.planning.info"),
                            Messages.getMessage("com.alcidauk.courses.planning.work.session.type.has.sessions"),
                            Messages.getMessage("com.alcidauk.courses.planning.confirm"),
                            Messages.getMessage("com.alcidauk.courses.planning.cancel"),
                            (ConfirmDialog.Listener) confirmDialog -> {
                                if (confirmDialog.isConfirmed()) {
                                    removeWorkSessionType(workSessionTypesGrid, beanItemContainer, workSessionTypeBeanItem);
                                } else {
                                    Notification.show(Messages.getMessage("com.alcidauk.courses.planning.info"),
                                            Messages.getMessage("com.alcidauk.courses.planning.work.session.type.cancel.removal"),
                                            Notification.Type.WARNING_MESSAGE);
                                }
                            });
                } else {
                    removeWorkSessionType(workSessionTypesGrid, beanItemContainer, workSessionTypeBeanItem);
                }
            }
        });

        mainLayout.addComponent(workSessionTypesGrid);
        mainLayout.addComponent(new HorizontalLayout(newWorkSessionTypeButton, removeSelectedSessionTypeButton));
        setContent(mainLayout);
    }

    private void removeWorkSessionType(Grid workSessionTypesGrid, BeanItemContainer<WorkSessionType> beanItemContainer,
                                       BeanItem<WorkSessionType> workSessionTypeBeanItem) {
        workSessionRepository.delete(workSessionTypeBeanItem.getBean().getWorkSessions());
        workSessionTypeRepository.delete(workSessionTypeBeanItem.getBean().getId());

        beanItemContainer.removeItem(workSessionTypeBeanItem);
        // TODO workaround to force grid to update (this actually does not work)
        workSessionTypesGrid.setContainerDataSource(beanItemContainer);
    }
}
