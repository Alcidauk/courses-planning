package com.alcidauk.ui;

import com.alcidauk.data.bean.WorkSessionType;
import com.alcidauk.data.repository.WorkSessionTypeRepository;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;

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

        Grid workSessionTypesGrid = new Grid("Matières");

        BeanItemContainer<WorkSessionType> beanItemContainer = new BeanItemContainer<>(WorkSessionType.class);
        beanItemContainer.addAll(workSessionTypeRepository.findNotSystem());

        workSessionTypesGrid.setContainerDataSource(beanItemContainer);
        workSessionTypesGrid.setEditorEnabled(true);
        workSessionTypesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
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
        newWorkSessionTypeButton.addClickListener((Button.ClickListener) clickEvent -> {
            WorkSessionType workSessionType = new WorkSessionType("Nouvelle matière", false);
            workSessionTypeRepository.save(workSessionType);
            beanItemContainer.addItem(workSessionType);
        });

        Button removeSelectedSessionTypeButton = new Button("Supprimer la matière sélectionnée");
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
