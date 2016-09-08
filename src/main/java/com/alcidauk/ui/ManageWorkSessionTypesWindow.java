package com.alcidauk.ui;

import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by alcidauk on 08/09/16.
 */
public class ManageWorkSessionTypesWindow extends Window {

    private static final Logger log = LoggerFactory.getLogger(ManageWorkSessionTypesWindow.class);

    private WorkSessionTypeRepository workSessionTypeRepository;

    public ManageWorkSessionTypesWindow(WorkSessionTypeRepository workSessionTypeRepository) {
        this.workSessionTypeRepository = workSessionTypeRepository;
    }

    public void init() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);

        Grid workSessionTypesGrid = new Grid("Matières");

        BeanItemContainer<WorkSessionType> beanItemContainer = new BeanItemContainer<>(WorkSessionType.class);
        beanItemContainer.addAll(workSessionTypeRepository.findAll());

        workSessionTypesGrid.setContainerDataSource(beanItemContainer);
        workSessionTypesGrid.setEditorEnabled(true);
        workSessionTypesGrid.setColumns("name");
        workSessionTypesGrid.getColumn("name").setHeaderCaption("Nom");
        workSessionTypesGrid.getColumn("name").setEditable(true);
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

        Button newWorkSessionTypeButton = new Button("Ajouter une matière");
        newWorkSessionTypeButton.addStyleName("margin-5");
        newWorkSessionTypeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                WorkSessionType workSessionType = new WorkSessionType("Nouvelle matière");
                workSessionTypeRepository.save(workSessionType);
                beanItemContainer.addItem(workSessionType);
            }
        });

        mainLayout.addComponent(workSessionTypesGrid);
        mainLayout.addComponent(newWorkSessionTypeButton);
        setContent(mainLayout);
    }
}
