package com.alcidauk.ui;

import com.alcidauk.app.Messages;
import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.*;

import java.util.Locale;

/**
 * Created by alcidauk on 08/09/16.
 */
public class ManageWorkSessionTypesWindow extends Window {

    private WorkSessionTypeRepository workSessionTypeRepository;

    public ManageWorkSessionTypesWindow(WorkSessionTypeRepository workSessionTypeRepository) {
        this.workSessionTypeRepository = workSessionTypeRepository;
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
                workSessionTypeRepository.delete(workSessionTypeBeanItem.getBean());
                beanItemContainer.removeItem(workSessionTypeBeanItem);
            }
        });

        mainLayout.addComponent(workSessionTypesGrid);
        mainLayout.addComponent(new HorizontalLayout(newWorkSessionTypeButton, removeSelectedSessionTypeButton));
        setContent(mainLayout);
    }
}
