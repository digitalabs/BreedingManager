package org.generationcp.breeding.manager.listmanager.dialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.customfields.BreedingLocationField;
import org.generationcp.breeding.manager.customfields.BreedingMethodField;
import org.generationcp.breeding.manager.customfields.ListDateField;
import org.generationcp.breeding.manager.listmanager.GermplasmSearchResultsComponent;
import org.generationcp.breeding.manager.listmanager.listeners.CloseWindowAction;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListButtonClickListener;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListItemClickListener;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListValueChangeListener;
import org.generationcp.breeding.manager.listmanager.sidebyside.GermplasmSearchBarComponent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class AddEntryDialog extends Window implements InitializingBean, InternationalizableComponent {
    
    private static final long serialVersionUID = -1627453790001229325L;
    
    private final static Logger LOG = LoggerFactory.getLogger(AddEntryDialog.class);
    
    public static final String OPTION_1_ID = "AddEntryDialog Option 1";
    public static final String OPTION_2_ID = "AddEntryDialog Option 2";
    public static final String OPTION_3_ID = "AddEntryDialog Option 3";
    public static final String NEXT_BUTTON_ID = "AddEntryDialog Next Button";
    public static final String CANCEL_BUTTON_ID = "AddEntryDialog Cancel Button";
    public static final String BACK_BUTTON_ID = "AddEntryDialog Back Button";
    public static final String DONE_BUTTON_ID = "AddEntryDialog Done Button";
    private static final String GID = "gid";
    private static final String DEFAULT_NAME_TYPE_CODE = "LNAME";
    private static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";

	public static final String GERMPLASM_BROWSER_LINK = "http://localhost:18080/GermplasmStudyBrowser/main/germplasm-";
    
    @Autowired
    private GermplasmDataManager germplasmDataManager;
    
    @Autowired
    private GermplasmListManager germplasmListManager;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Window parentWindow;
    private VerticalLayout topPart;
    private AbsoluteLayout bottomPart;
    private AddEntryDialogSource source;
    private OptionGroup optionGroup;
    private List<Integer> selectedGids;
    
    private BreedingMethodField breedingMethodField;
    private BreedingLocationField breedingLocationField;
    
    private Button doneButton;
    private Button cancelButton;
    
    private Label germplasmDateLabel;
    private Label nameTypeLabel;
    private Label bottomPartHeader;
    
    private ComboBox nameTypeComboBox;
    
    private ListDateField germplasmDateField;

    private GermplasmSearchBarComponent searchBarComponent;
    
    private GermplasmSearchResultsComponent searchResultsComponent;
    
    public AddEntryDialog(AddEntryDialogSource source, Window parentWindow){
        this.source = source;
        this.parentWindow = parentWindow;
    }
    


    @Override
    public void afterPropertiesSet() throws Exception {
        // set as modal window, other components are disabled while window is open
        setModal(true);
        // define window size, set as not resizable
        setWidth("800px");
        setResizable(false);
        setCaption(messageSource.getMessage(Message.ADD_LIST_ENTRIES));
        // center window within the browser
        center();
        
        assembleTopPart();
        
        assembleBottomPart();
        setSpeficyDetailsVisible(false);
        
        assembleButtonLayout();
    }
    
    public void resultTableItemClickAction(Table sourceTable) throws InternationalizableException {
    	
    	this.selectedGids = getSelectedItemIds(sourceTable);
    	
    	if(selectedGids.size()>0){
    		this.doneButton.setEnabled(true);
    	} else {
    		this.doneButton.setEnabled(false);    		
    	}
    }
    
    public void resultTableValueChangeAction() throws InternationalizableException {
    	this.selectedGids = getSelectedItemIds(searchResultsComponent.getMatchingGermplasmsTable());
    	if(doneButton!=null){
	    	if(selectedGids.size()>0){
	    		this.doneButton.setEnabled(true);
	    	} else {
	    		this.doneButton.setEnabled(false);    		
	    	}
    	}
    }    
    
    public void resultTableItemDoubleClickAction(Table sourceTable, Object itemId, Item item) throws InternationalizableException {
        sourceTable.select(itemId);
        int gid = Integer.valueOf(item.getItemProperty(GID).getValue().toString());
        
        Tool tool = null;
        try {
            tool = workbenchDataManager.getToolWithName(ToolName.germplasm_browser.toString());
        } catch (MiddlewareQueryException qe) {
            LOG.error(messageSource.getMessage(Message.QUERY_EXCEPTION), qe);
        }
        
        ExternalResource germplasmBrowserLink = null;
        if (tool == null) {
            germplasmBrowserLink = new ExternalResource(GERMPLASM_BROWSER_LINK + gid);
        } else {
            germplasmBrowserLink = new ExternalResource(tool.getPath().replace("germplasm/", "germplasm-") + gid);
        }
        
        Window germplasmWindow = new Window(messageSource.getMessage(Message.GERMPLASM_INFORMATION) + " - " + gid);
        
        VerticalLayout layoutForGermplasm = new VerticalLayout();
        layoutForGermplasm.setMargin(false);
        //layoutForGermplasm.setWidth("640px");
        //layoutForGermplasm.setHeight("560px");
        layoutForGermplasm.setWidth("98%");
        layoutForGermplasm.setHeight("98%");
        
        Embedded germplasmInfo = new Embedded("", germplasmBrowserLink);
        germplasmInfo.setType(Embedded.TYPE_BROWSER);
        germplasmInfo.setSizeFull();
        layoutForGermplasm.addComponent(germplasmInfo);
        
        germplasmWindow.setContent(layoutForGermplasm);
        //germplasmWindow.setWidth("645px");
        //germplasmWindow.setHeight("600px");
        germplasmWindow.setWidth("90%");
        germplasmWindow.setHeight("90%");
        germplasmWindow.center();
        germplasmWindow.setResizable(false);
        
        germplasmWindow.setModal(true);
        
        this.parentWindow.addWindow(germplasmWindow);
    }
    
    private void assembleTopPart(){
        topPart = new VerticalLayout();
        topPart.setSpacing(true);
        topPart.setMargin(false);
        
        Label topPartHeader = new Label(messageSource.getMessage(Message.SELECT_A_GERMPLASM));
        topPartHeader.addStyleName("bold");
        topPartHeader.addStyleName("h3");
        topPart.addComponent(topPartHeader);
        
        searchResultsComponent = new GermplasmSearchResultsComponent(null, false, false);
        searchResultsComponent.getMatchingGermplasmsTable().setHeight("150px");
        searchResultsComponent.getMatchingGermplasmsTableWithSelectAll().setHeight("180px");
        searchResultsComponent.setRightClickActionHandlerEnabled(false);
        
        searchResultsComponent.getMatchingGermplasmsTable().addListener(new GermplasmListValueChangeListener(this));
        searchResultsComponent.getMatchingGermplasmsTable().addListener(new GermplasmListItemClickListener(this));
        
        searchBarComponent = new GermplasmSearchBarComponent(searchResultsComponent);
        
        topPart.addComponent(searchBarComponent);
        
        topPart.addComponent(searchResultsComponent);
        
        Label step2Label = new Label(messageSource.getMessage(Message.HOW_DO_YOU_WANT_TO_ADD_THE_GERMPLASM_TO_THE_LIST));
        step2Label.addStyleName("bold");
        topPart.addComponent(step2Label);
        
        optionGroup = new OptionGroup();
        optionGroup.addItem(OPTION_1_ID);
        optionGroup.setItemCaption(OPTION_1_ID, messageSource.getMessage(Message.USE_SELECTED_GERMPLASM_FOR_THE_LIST_ENTRY));
        optionGroup.addItem(OPTION_2_ID);
        optionGroup.setItemCaption(OPTION_2_ID, messageSource.getMessage(Message.CREATE_A_NEW_GERMPLASM_RECORD_FOR_THE_LIST_ENTRY_AND_ASSIGN_THE_SELECTED_GERMPLASM_AS_ITS_SOURCE));
        optionGroup.addItem(OPTION_3_ID);
        optionGroup.setItemCaption(OPTION_3_ID, messageSource.getMessage(Message.CREATE_A_NEW_GERMPLASM_RECORD_FOR_THE_LIST_ENTRY));
        optionGroup.select(OPTION_1_ID);
        optionGroup.setImmediate(true);
        optionGroup.addListener(new Property.ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if(optionGroup.getValue().equals(OPTION_1_ID)){
                    setSpeficyDetailsVisible(false);
                    if(selectedGids.size()==0){
                        doneButton.setEnabled(false);
                    }
                } else if(optionGroup.getValue().equals(OPTION_2_ID)){
                    setSpeficyDetailsVisible(true);
                    if(selectedGids.size()==0){
                        doneButton.setEnabled(false);
                    } else {
                        doneButton.setEnabled(true);
                    }
                } else if(optionGroup.getValue().equals(OPTION_3_ID)){
                    doneButton.setEnabled(true);
                    setSpeficyDetailsVisible(true);
                }
            }
        });
        topPart.addComponent(optionGroup);
        
        addComponent(topPart);
    }
    
    private void assembleBottomPart(){
        bottomPart = new AbsoluteLayout();
        bottomPart.setWidth("600px");
        bottomPart.setHeight("230px");
        
        bottomPartHeader = new Label(messageSource.getMessage(Message.SPECIFY_ADDITIONAL_DETAILS));
        bottomPartHeader.addStyleName("bold");
        bottomPartHeader.addStyleName("h3");
        bottomPart.addComponent(bottomPartHeader, "top:15px;left:0px");
        
        breedingMethodField = new BreedingMethodField(parentWindow);
        bottomPart.addComponent(breedingMethodField, "top:50px;left:0px");
                
        germplasmDateLabel = new Label("Creation Date: ");
        germplasmDateLabel.addStyleName("bold");
        bottomPart.addComponent(germplasmDateLabel, "top:107px;left:0px");
        
        germplasmDateField =  new ListDateField("", false);
        germplasmDateField.getListDtDateField().setValue(new Date());
        bottomPart.addComponent(germplasmDateField, "top:102px;left:124px");
        
        breedingLocationField = new BreedingLocationField(parentWindow);
        bottomPart.addComponent(breedingLocationField, "top:133px;left:0px");
        
        nameTypeLabel = new Label("Name Type: ");
        nameTypeLabel.addStyleName("bold");
        bottomPart.addComponent(nameTypeLabel, "top:185px;left:0px");
        
        nameTypeComboBox = new ComboBox();
        nameTypeComboBox.setWidth("400px");
        bottomPart.addComponent(nameTypeComboBox, "top:185px;left:130px");
        populateNameTypeComboBox();
        
        addComponent(bottomPart);
    }
    
    
    public void assembleButtonLayout(){
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setHeight("50px");
        buttonLayout.setSpacing(true);
        
        cancelButton = new Button(messageSource.getMessage(Message.CANCEL));
        cancelButton.setData(CANCEL_BUTTON_ID);
        cancelButton.addListener(new CloseWindowAction());
        buttonLayout.addComponent(cancelButton);
        
        doneButton = new Button(messageSource.getMessage(Message.DONE));
        doneButton.setData(DONE_BUTTON_ID);
        doneButton.addListener(new GermplasmListButtonClickListener(this));
        doneButton.setEnabled(false);
        doneButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        buttonLayout.addComponent(doneButton);
        
        buttonLayout.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
        buttonLayout.setComponentAlignment(doneButton, Alignment.BOTTOM_LEFT);
        
        addComponent(buttonLayout);
    }
    
    public void nextButtonClickAction(ClickEvent event){
    	
        if(optionGroup.getValue().equals(OPTION_1_ID)){
            // add the germplasm selected as the list entry
            if(this.selectedGids.size()>0){
                this.source.finishAddingEntry(selectedGids);
                Window window = event.getButton().getWindow();
                window.getParent().removeWindow(window);
            } else {
                MessageNotifier.showWarning(this, messageSource.getMessage(Message.WARNING), 
                        messageSource.getMessage(Message.YOU_MUST_SELECT_A_GERMPLASM_FROM_THE_SEARCH_RESULTS), Notification.POSITION_CENTERED);
            }
        } else if(optionGroup.getValue().equals(OPTION_2_ID)){
        	if(breedingMethodField.getBreedingMethodComboBox().getValue() == null){
        		MessageNotifier.showError(this, messageSource.getMessage(Message.ERROR), 
                       messageSource.getMessage(Message.YOU_MUST_SELECT_A_METHOD_FOR_THE_GERMPLASM), Notification.POSITION_CENTERED);
        	} else if(breedingLocationField.getBreedingLocationComboBox().getValue() == null){
            		MessageNotifier.showError(this, messageSource.getMessage(Message.ERROR), 
                            messageSource.getMessage(Message.YOU_MUST_SELECT_A_LOCATION_FOR_THE_GERMPLASM), Notification.POSITION_CENTERED);
        	}else if(this.selectedGids.size()>0){
            	if(doneAction()){
            		Window window = event.getButton().getWindow();
            		window.getParent().removeWindow(window);
            	}
            } else{
                MessageNotifier.showWarning(this, messageSource.getMessage(Message.WARNING), 
                        messageSource.getMessage(Message.YOU_MUST_SELECT_A_GERMPLASM_FROM_THE_SEARCH_RESULTS), Notification.POSITION_CENTERED);
            }
        } else if(optionGroup.getValue().equals(OPTION_3_ID)){
            String searchValue = searchBarComponent.getSearchField().getValue().toString();
            
        	if(breedingMethodField.getBreedingMethodComboBox().getValue() == null){
        		MessageNotifier.showError(this, messageSource.getMessage(Message.ERROR), 
        				messageSource.getMessage(Message.YOU_MUST_SELECT_A_METHOD_FOR_THE_GERMPLASM), Notification.POSITION_CENTERED);
        	} else if(breedingLocationField.getBreedingLocationComboBox().getValue() == null){
            		MessageNotifier.showError(this, messageSource.getMessage(Message.ERROR), 
            				messageSource.getMessage(Message.YOU_MUST_SELECT_A_LOCATION_FOR_THE_GERMPLASM), Notification.POSITION_CENTERED);
        	} else if(searchValue != null && searchValue.length() != 0){
            	doneAction();
            	Window window = event.getButton().getWindow();
            	window.getParent().removeWindow(window);
            } else {
                MessageNotifier.showWarning(this, messageSource.getMessage(Message.WARNING), 
                		messageSource.getMessage(Message.YOU_MUST_ENTER_A_GERMPLASM_NAME_IN_THE_TEXTBOX),
                        Notification.POSITION_CENTERED);
            }
        }
    }
    
    public void backButtonClickAction(){
    }
    
    public Boolean doneAction(){
    	
        if(this.optionGroup.getValue().equals(OPTION_2_ID) || this.optionGroup.getValue().equals(OPTION_3_ID)){
        	
	        Integer breedingMethodId = (Integer) this.breedingMethodField.getBreedingMethodComboBox().getValue();
	        Integer nameTypeId = (Integer) this.nameTypeComboBox.getValue();
	        Integer locationId = (Integer) this.breedingLocationField.getBreedingLocationComboBox().getValue();
	        Date dateOfCreation = (Date) this.germplasmDateField.getValue();
	        SimpleDateFormat formatter = new SimpleDateFormat(DATE_AS_NUMBER_FORMAT);
	        
	        if(dateOfCreation==null){
	            LOG.error("Invalid date on add list entries! - " + dateOfCreation);
	            MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR), messageSource.getMessage(Message.VALIDATION_DATE_FORMAT), Notification.POSITION_CENTERED);
	            return false;
	        }
	        String parsedDate = formatter.format(dateOfCreation);
	        if(parsedDate==null){
	            LOG.error("Invalid date on add list entries! - " + parsedDate);
	            MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR), messageSource.getMessage(Message.VALIDATION_DATE_FORMAT), Notification.POSITION_CENTERED);
	            return false;
	        }
	        
	        Integer date = Integer.parseInt(parsedDate);
            String germplasmName = searchBarComponent.getSearchField().getValue().toString();
	        
	        if(this.optionGroup.getValue().equals(OPTION_2_ID)){
		        
	        	List<Integer> addedGids = new ArrayList<Integer>();
	        	
		        for(Integer selectedGid : selectedGids){
		        
		        	Germplasm selectedGermplasm = null;
		        	
	                try{
	                    selectedGermplasm = this.germplasmDataManager.getGermplasmWithPrefName(selectedGid);
	                    if(selectedGermplasm.getPreferredName() != null){
	                        germplasmName = selectedGermplasm.getPreferredName().getNval();
	                    }
	                } catch(MiddlewareQueryException mex){
	                    LOG.error("Error with getting germplasm with id: " + selectedGid, mex);
	                    MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), messageSource.getMessage(Message.ERROR_WITH_GETTING_GERMPLASM_WITH_ID)+": " + selectedGid 
	                            +". "+messageSource.getMessage(Message.ERROR_REPORT_TO)
	                            , Notification.POSITION_CENTERED);
	                }
			        
			        
			        Integer userId = Integer.valueOf(getCurrentUserLocalId());
			                
			        Germplasm germplasm = new Germplasm();
			        germplasm.setGdate(date);
			        germplasm.setGnpgs(Integer.valueOf(-1));
			        germplasm.setGpid1(Integer.valueOf(0));
			        germplasm.setGrplce(Integer.valueOf(0));
			        germplasm.setLgid(Integer.valueOf(0));
			        germplasm.setLocationId(locationId);
			        germplasm.setMethodId(breedingMethodId);
			        germplasm.setMgid(Integer.valueOf(0));
			        germplasm.setReferenceId(Integer.valueOf(0));
			        germplasm.setUserId(userId);
			         
		        	if(selectedGermplasm != null){
		                if(selectedGermplasm.getGnpgs()<2){
		                	germplasm.setGpid1(selectedGermplasm.getGpid1());
		                } else {
		                	germplasm.setGpid1(selectedGermplasm.getGid());                            	
		                }
		        	}
		            germplasm.setGpid2(selectedGid);
			        
			        Name name = new Name();
			        name.setNval(germplasmName);
			        name.setLocationId(locationId);
			        name.setNdate(date);
			        name.setNstat(Integer.valueOf(1));
			        name.setReferenceId(Integer.valueOf(0));
			        name.setTypeId(nameTypeId);
			        name.setUserId(userId);
			        
			        try{
			            Integer gid = this.germplasmDataManager.addGermplasm(germplasm, name);
			            addedGids.add(germplasm.getGid());
			        } catch(MiddlewareQueryException ex){
			            LOG.error("Error with saving germplasm and name records!", ex);
			            MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), messageSource.getMessage(Message.ERROR_WITH_SAVING_GERMPLASM_AND_NAME_RECORDS) + messageSource.getMessage(Message.ERROR_REPORT_TO)
			                    , Notification.POSITION_CENTERED);
			            return false;
			        }
	        	}

		        this.source.finishAddingEntry(addedGids);
		        
		        return true;
		        
	        } else {
		        Integer userId = Integer.valueOf(getCurrentUserLocalId());
                
		        Germplasm germplasm = new Germplasm();
		        germplasm.setGdate(date);
		        germplasm.setGnpgs(Integer.valueOf(-1));
		        germplasm.setGpid1(Integer.valueOf(0));
		        germplasm.setGpid2(Integer.valueOf(0));
		        germplasm.setGrplce(Integer.valueOf(0));
		        germplasm.setLgid(Integer.valueOf(0));
		        germplasm.setLocationId(locationId);
		        germplasm.setMethodId(breedingMethodId);
		        germplasm.setMgid(Integer.valueOf(0));
		        germplasm.setReferenceId(Integer.valueOf(0));
		        germplasm.setUserId(userId);
	        	
		        Name name = new Name();
		        name.setNval(germplasmName);
		        name.setLocationId(locationId);
		        name.setNdate(date);
		        name.setNstat(Integer.valueOf(1));
		        name.setReferenceId(Integer.valueOf(0));
		        name.setTypeId(nameTypeId);
		        name.setUserId(userId);
		        
		        try{
		            Integer gid = this.germplasmDataManager.addGermplasm(germplasm, name);
		            this.source.finishAddingEntry(gid);
		            return true;
		        } catch(MiddlewareQueryException ex){
		            LOG.error("Error with saving germplasm and name records!", ex);
		            MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), messageSource.getMessage(Message.ERROR_WITH_SAVING_GERMPLASM_AND_NAME_RECORDS) + messageSource.getMessage(Message.ERROR_REPORT_TO)
		                    , Notification.POSITION_CENTERED);
		            return false;
		        }
	        }
        
    	}
        
        return false;
    }
    
    private void populateNameTypeComboBox(){
        try{
            List<UserDefinedField> nameTypes = this.germplasmListManager.getGermplasmNameTypes();
            for(UserDefinedField nameType : nameTypes){
                Integer nameTypeId = nameType.getFldno();
                String nameTypeString = nameType.getFname();
                String nameTypeCode = nameType.getFcode();
                this.nameTypeComboBox.addItem(nameTypeId);
                this.nameTypeComboBox.setItemCaption(nameTypeId, nameTypeString);
                if(nameTypeCode.equals(DEFAULT_NAME_TYPE_CODE)){
                    this.nameTypeComboBox.select(nameTypeId);
                }
            }
        } catch (MiddlewareQueryException ex){
            LOG.error("Error with getting germplasm name types!", ex);
            MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), messageSource.getMessage(Message.ERROR_WITH_GETTING_GERMPLASM_NAME_TYPES) + messageSource.getMessage(Message.ERROR_REPORT_TO)
                    , Notification.POSITION_CENTERED);
            Integer unknownId = Integer.valueOf(0);
            this.nameTypeComboBox.addItem(unknownId);
            this.nameTypeComboBox.setItemCaption(unknownId, messageSource.getMessage(Message.UNKNOWN));
        }
        this.nameTypeComboBox.setNullSelectionAllowed(false);
    }
    
    private int getCurrentUserLocalId(){
        try{
            Integer workbenchUserId = this.workbenchDataManager.getWorkbenchRuntimeData().getUserId();
            Project lastProject = this.workbenchDataManager.getLastOpenedProject(workbenchUserId);
            Integer localIbdbUserId = this.workbenchDataManager.getLocalIbdbUserId(workbenchUserId,lastProject.getProjectId());
            if (localIbdbUserId != null) {
                return localIbdbUserId;
            } else {
                return -1;
            }
       } catch(MiddlewareQueryException ex){
           LOG.error("Error with getting local IBDB user!", ex);
               MessageNotifier.showError(getWindow(), "Database Error!", messageSource.getMessage(Message.ERROR_REPORT_TO)
                       , Notification.POSITION_CENTERED);
           return -1;
       }
    }
    
    @Override
    public void updateLabels() {
    }

    /**
     * Iterates through the whole table, gets selected item ID's, make sure it's sorted as seen on the UI
     */
    @SuppressWarnings("unchecked")
    private List<Integer> getSelectedItemIds(Table table){
        List<Integer> itemIds = new ArrayList<Integer>();
        List<Integer> selectedItemIds = new ArrayList<Integer>();
        List<Integer> trueOrderedSelectedItemIds = new ArrayList<Integer>();
        
        selectedItemIds.addAll((Collection<? extends Integer>) table.getValue());
        itemIds = getItemIds(table);
            
        for(Integer itemId: itemIds){
            if(selectedItemIds.contains(itemId)){
                trueOrderedSelectedItemIds.add(itemId);
            }
        }
        
        return trueOrderedSelectedItemIds;
    }    

    /**
     * Get item id's of a table, and return it as a list 
     * @param table
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Integer> getItemIds(Table table){
        List<Integer> itemIds = new ArrayList<Integer>();
        itemIds.addAll((Collection<? extends Integer>) table.getItemIds());
        return itemIds;
    }    
    
    
    private void setSpeficyDetailsVisible(Boolean visible){
        int height = 530;
    	if(visible){
    	    height += 230 + 10; // add height of bottom part + margin
    		setHeight(height + "px");
    		bottomPart.setVisible(true);
    		center();
    	} else {
    		setHeight(height + "px");
    		bottomPart.setVisible(false);
    		center();
    	}
    }
    
    public void focusOnSearchField(){
        searchBarComponent.getSearchField().focus();
    }
    
}