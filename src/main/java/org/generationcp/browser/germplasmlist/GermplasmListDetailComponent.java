/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.browser.germplasmlist;

import java.util.Date;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.germplasmlist.listeners.GermplasmListButtonClickListener;
import org.generationcp.browser.util.Util;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window.Notification;

@Configurable
public class GermplasmListDetailComponent extends GridLayout implements InitializingBean, InternationalizableComponent {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDetailComponent.class);
    private static final long serialVersionUID = 1738426765643928293L;

    private Label lblName;
    private Label lblDescription;
    private Label lblCreationDate;
    private Label lblType;
    private Label lblStatus;
    private Label lblListOwner;
    
    private Label listName;
    private Label listDescription;
    private Label listCreationDate;
    private Label listType;
    private Label listStatus;
    private Label listOwner;
    
    private Button lockButton;
    private Button unlockButton;
    private Button deleteButton;
    
    public static String LOCK_BUTTON_ID = "Lock Germplasm List";
    public static String UNLOCK_BUTTON_ID = "Unlock Germplasm List";
    public static String DELETE_BUTTON_ID = "Delete Germplasm List";

    private GermplasmListManager germplasmListManager;
    private int germplasmListId;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private UserDataManager userDataManager;
    
    public GermplasmList germplasmList;
    public GermplasmListAccordionMenu germplasmListAccordionMenu;
    
    private boolean usedForDetailsOnly;
    
    public GermplasmListDetailComponent(GermplasmListManager germplasmListManager, int germplasmListId, boolean usedForDetailsOnly){
        this.germplasmListManager = germplasmListManager;
        this.germplasmListId = germplasmListId;
        this.usedForDetailsOnly = usedForDetailsOnly;
    }

    public GermplasmListDetailComponent(GermplasmListAccordionMenu germplasmListAccordionMenu, GermplasmListManager germplasmListManager, int germplasmListId
            , boolean usedForDetailsOnly){
        this.germplasmListAccordionMenu = germplasmListAccordionMenu;
        this.germplasmListManager = germplasmListManager;
        this.germplasmListId = germplasmListId;
        this.usedForDetailsOnly = usedForDetailsOnly;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception{
        setRows(8);
        setColumns(6);
        setColumnExpandRatio(0, 1);
        setColumnExpandRatio(1, 1);
        setSpacing(true);
        setMargin(true);

        lblName = new Label(messageSource.getMessage(Message.NAME_LABEL)); // "Name"
        lblDescription = new Label(messageSource.getMessage(Message.DESCRIPTION_LABEL)); // "Description"
        lblCreationDate = new Label(messageSource.getMessage(Message.CREATION_DATE_LABEL) + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", Label.CONTENT_XHTML); // "Creation Date"
        lblType = new Label(messageSource.getMessage(Message.TYPE_LABEL)); // "Type"
        lblStatus = new Label(messageSource.getMessage(Message.STATUS_LABEL)); // "Status"
        lblListOwner = new Label(messageSource.getMessage(Message.LIST_OWNER_LABEL)); // "List Owner"
        
        lblName.addStyleName("gcp-form-label");
        lblDescription.addStyleName("gcp-form-label");
        lblCreationDate.addStyleName("gcp-form-label");
        lblType.addStyleName("gcp-form-label");
        lblStatus.addStyleName("gcp-form-label");
        lblListOwner.addStyleName("gcp-form-label");
        
        // get GermplasmList Detail
        germplasmList = germplasmListManager.getGermplasmListById(germplasmListId);

        listName = new Label(germplasmList.getName());
        listDescription = new Label(germplasmList.getDescription());
        listCreationDate = new Label(String.valueOf(germplasmList.getDate()));
        listType = new Label(germplasmList.getType());
        listStatus = new Label(germplasmList.getStatusString());
        listOwner= new Label(getOwnerListName(germplasmList.getUserId()));
        
        addComponent(lblName, 0, 0, 2, 0);
        addComponent(lblDescription, 0, 1, 2, 1);
        addComponent(lblCreationDate, 0, 2, 2, 2);
        addComponent(lblType, 0, 3, 2, 3);
        addComponent(lblStatus, 0, 4, 2, 4);
        addComponent(lblListOwner, 0, 5, 2, 5);
        
        addComponent(listName, 3, 0, 5, 0);
        addComponent(listDescription, 3, 1, 5, 1);
        addComponent(listCreationDate, 3, 2, 5, 2);
        addComponent(listType, 3, 3, 5, 3);
        addComponent(listStatus, 3, 4, 5, 4);
        addComponent(listOwner, 3, 5, 5, 5);
        
        Long projectId = (long) workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue();
        workbenchDataManager.getWorkbenchRuntimeData();
        Integer workbenchUserId = workbenchDataManager.getWorkbenchRuntimeData().getUserId();
        Integer IBDBUserId = workbenchDataManager.getLocalIbdbUserId(workbenchUserId, projectId);
        
        if(!usedForDetailsOnly){
            if(germplasmList.getUserId().equals(IBDBUserId) && germplasmList.getId()<0){
                if(germplasmList.getStatus()>=100){
                    unlockButton = new Button("Unlock");
                    unlockButton.setData(UNLOCK_BUTTON_ID);
                    unlockButton.addListener(new GermplasmListButtonClickListener(this, germplasmList));
                    unlockButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                    addComponent(unlockButton, 0, 7);
                } else if(germplasmList.getStatus()==1) {
                    lockButton = new Button("Lock");
                    lockButton.setData(LOCK_BUTTON_ID);
                    lockButton.addListener(new GermplasmListButtonClickListener(this, germplasmList));
                    lockButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                    addComponent(lockButton, 0, 7);
                    
                    deleteButton = new Button("Delete");
                    deleteButton.setData(DELETE_BUTTON_ID);
                    deleteButton.addListener(new GermplasmListButtonClickListener(this, germplasmList));
                    deleteButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
                    addComponent(deleteButton, 1, 7);
                }
            }
        }
    }
    
    private String getOwnerListName(Integer userId) throws MiddlewareQueryException {
        User user=userDataManager.getUserById(userId);
        if(user != null){
            int personId=user.getPersonid();
            Person p =userDataManager.getPersonById(personId);
    
            if(p!=null){
                return p.getFirstName()+" "+p.getMiddleName() + " "+p.getLastName();
            }else{
                return user.getName();
            }
        } else {
            return "";
        }
    }

    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }

    @Override
    public void updateLabels() {
        
    }

    public void lockGermplasmList() {
        if(germplasmList.getStatus()<100){
            germplasmList.setStatus(germplasmList.getStatus()+100);
            try {
                germplasmListManager.updateGermplasmList(germplasmList);

                User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());
                ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                        workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
                        "Locked a germplasm list.", 
                        "Locked list "+germplasmList.getId()+" - "+germplasmList.getName(),
                        user,
                        new Date());
                workbenchDataManager.addProjectActivity(projAct);
                
                Tab tab = Util.getTabAlreadyExist(germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList(), germplasmList.getName());
                germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList().removeTab(tab);
                
                germplasmListAccordionMenu.getGermplasmListTreeComponent().createGermplasmListInfoTab(germplasmListId);
                tab = Util.getTabAlreadyExist(germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList(), germplasmList.getName());
                germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList().setSelectedTab(tab.getComponent());
                
                //getWindow().getWindow().showNotification("Germplasm List", "Successfully Locked", Notification.TYPE_WARNING_MESSAGE);
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();
            }
        }
        lockButton.detach();

        deleteButton.setEnabled(false);                
    }
    
    public void unlockGermplasmList() {
        if(germplasmList.getStatus()>=100){
            germplasmList.setStatus(germplasmList.getStatus()-100);
            try {
                germplasmListManager.updateGermplasmList(germplasmList);

                Tab tab = Util.getTabAlreadyExist(germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList(), germplasmList.getName());
                germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList().removeTab(tab);
                
                germplasmListAccordionMenu.getGermplasmListTreeComponent().createGermplasmListInfoTab(germplasmListId);
                tab = Util.getTabAlreadyExist(germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList(), germplasmList.getName());
                germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList().setSelectedTab(tab.getComponent());
                
                User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());
                ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                        workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
                        "Unlocked a germplasm list.", 
                        "Unlocked list "+germplasmList.getId()+" - "+germplasmList.getName(),
                        user,
                        new Date());
                workbenchDataManager.addProjectActivity(projAct);
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();
            }
        }
    }    
    
    public void deleteGermplasmList() {
        ConfirmDialog.show(this.getWindow(), "Delete Germplasm List:", "Do you want to delete this germplasm list?", "Yes", "No", new ConfirmDialog.Listener() {
                    private static final long serialVersionUID = 1L;

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                    deleteGermplasmListConfirmed();
                }
            }
        });
    }
    
    public void deleteGermplasmListConfirmed() {
        if(germplasmList.getStatus()<100){ 
            try {
                germplasmListManager.deleteGermplasmList(germplasmList);
                
                User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());
                ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                        workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
                        "Deleted a germplasm list.", 
                        "Deleted germplasm list with id = "+germplasmList.getId()+" and name = "+germplasmList.getName()+".",
                        user,
                        new Date());
                workbenchDataManager.addProjectActivity(projAct);
                lockButton.setEnabled(false);
                deleteButton.setEnabled(false);
                getWindow().showNotification("Germplasm List", "Successfully deleted", Notification.TYPE_WARNING_MESSAGE);
                //Close confirmation window
                
                //Re-use refresh action on GermplasmListTreeComponent
                germplasmListAccordionMenu.getGermplasmListTreeComponent().createTree();
                
                //Close tab
                Tab tab = Util.getTabAlreadyExist(germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList(), germplasmList.getName());
                germplasmListAccordionMenu.getGermplasmListTreeComponent().getTabSheetGermplasmList().removeTab(tab);
                
                
                
            } catch (MiddlewareQueryException e) {
                getWindow().showNotification("Error", "There was a problem deleting the germplasm list", Notification.TYPE_ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }                
}