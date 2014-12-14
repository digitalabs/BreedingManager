package org.generationcp.breeding.manager.customcomponent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.crossingmanager.ParentTabComponent;
import org.generationcp.breeding.manager.crossingmanager.listeners.SelectTreeItemOnSaveListener;
import org.generationcp.breeding.manager.customfields.BreedingManagerListDetailsComponent;
import org.generationcp.breeding.manager.customfields.ListDateField;
import org.generationcp.breeding.manager.customfields.LocalListFoldersTreeComponent;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.ReserveInventorySource;
import org.generationcp.breeding.manager.listmanager.ListBuilderComponent;
import org.generationcp.breeding.manager.listmanager.listeners.CloseWindowAction;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class SaveListAsDialog extends BaseSubWindow implements InitializingBean, InternationalizableComponent, BreedingManagerLayout{

	private static final String FOLDER_TYPE = "FOLDER";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SaveListAsDialog.class);
	
	private CssLayout mainLayout;
	private HorizontalLayout contentLayout;
	private HorizontalLayout buttonLayout;
	
	private final SaveListAsDialogSource source;
	
	private Label guideMessage;
	private LocalListFoldersTreeComponent germplasmListTree;
	private BreedingManagerListDetailsComponent listDetailsComponent;

	private Button cancelButton;
	private Button saveButton;
	
	private final String windowCaption;
	private boolean showFoldersOnlyInListTree = false;
	
	@SuppressWarnings("unused")
	private String defaultListType;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
    private GermplasmListManager germplasmListManager;
	
	private GermplasmList originalGermplasmList;
	private GermplasmList germplasmList;
	
	public static final Integer LIST_NAMES_STATUS = 1;
	
	public SaveListAsDialog(SaveListAsDialogSource source, GermplasmList germplasmList){
		this.source = source;
		this.originalGermplasmList = germplasmList;
		this.germplasmList = germplasmList;
		this.windowCaption = null;
	}
	
	public SaveListAsDialog(SaveListAsDialogSource source, String defaultListType, GermplasmList germplasmList){
		this.source = source;
		this.originalGermplasmList = germplasmList;
		this.germplasmList = germplasmList;
		this.defaultListType = defaultListType;
		this.windowCaption = null;
	}

	public SaveListAsDialog(SaveListAsDialogSource source, GermplasmList germplasmList, String windowCaption){
		this.source = source;
		this.originalGermplasmList = germplasmList;
		this.germplasmList = germplasmList;
		this.windowCaption = windowCaption;
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
	}
	
	@Override
	public void instantiateComponents() {
		if(windowCaption == null){
			setCaption(messageSource.getMessage(Message.SAVE_LIST_AS));
		} else{
			setCaption(windowCaption);
		}
		
		addStyleName(Reindeer.WINDOW_LIGHT);
		setResizable(false);
		setModal(true);
		
		if(germplasmList!=null){
			germplasmListTree = new LocalListFoldersTreeComponent(new SelectTreeItemOnSaveListener(this,source.getParentComponent()), germplasmList.getId(), 
					isShowFoldersOnlyInListTree(), true);
		} else{
			germplasmListTree = new LocalListFoldersTreeComponent(new SelectTreeItemOnSaveListener(this,source.getParentComponent()), 
					null, isShowFoldersOnlyInListTree(), true);
		}
		
		guideMessage = new Label(messageSource.getMessage(Message.SELECT_A_FOLDER_TO_CREATE_A_LIST_OR_SELECT_AN_EXISTING_LIST_TO_EDIT_AND_OVERWRITE_ITS_ENTRIES)+".");
		
		listDetailsComponent = new BreedingManagerListDetailsComponent(defaultListType(), germplasmList);
		
		cancelButton = new Button(messageSource.getMessage(Message.CANCEL));
		cancelButton.setWidth("80px");
		
		saveButton = new Button(messageSource.getMessage(Message.SAVE_LABEL));
		saveButton.setWidth("80px");
		saveButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		saveButton.setDebugId("vaadin-save-btn");
	}
	
	public String defaultListType(){
		return "LST";
	}

	@Override
	public void initializeValues() {
		if(germplasmList != null){		
			germplasmListTree.createTree();
			listDetailsComponent.setGermplasmListDetails(germplasmList);
		} else {
			listDetailsComponent.setGermplasmListDetails(null);
		}
	}

	@Override
	public void addListeners() {
		cancelButton.addListener(new CloseWindowAction());
		saveButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 993268331611479850L;
			@Override
			public void buttonClick(final ClickEvent event) {
				doSaveAction(event);
			}			
		});
	}

	@Override
	public void layoutComponents() {
		setWidth("740px");
		setHeight("510px");
		
		contentLayout = new HorizontalLayout();
		contentLayout.setSpacing(true);
		contentLayout.addComponent(germplasmListTree);
		contentLayout.addComponent(listDetailsComponent);
		contentLayout.addStyleName("contentLayout");

		contentLayout.setWidth("714px");
		contentLayout.setHeight("356px");
		
		listDetailsComponent.addStyleName("listDetailsComponent");
		
		buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.setMargin(true);
		buttonLayout.addComponent(cancelButton);
		buttonLayout.addComponent(saveButton);
		buttonLayout.addStyleName("buttonLayout");
		
		HorizontalLayout buttonLayoutMain = new HorizontalLayout();
		buttonLayoutMain.addComponent(buttonLayout);
		buttonLayoutMain.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		buttonLayoutMain.setWidth("100%");
		buttonLayoutMain.setHeight("50px");
		buttonLayoutMain.addStyleName("buttonLayoutMain");
		
		mainLayout = new CssLayout();
		mainLayout.setWidth("741px");
		mainLayout.setHeight("420px");
		mainLayout.addComponent(guideMessage);
		mainLayout.addComponent(contentLayout);
		mainLayout.addComponent(buttonLayoutMain);
		mainLayout.addStyleName("mainlayout");
		
		addComponent(mainLayout);
	}

	@Override
	public void updateLabels() {
		// do nothing
	}
	
	public GermplasmList getSelectedListOnTree(){
		Integer folderId = null;
		if(germplasmListTree.getSelectedListId() instanceof Integer){
			folderId = (Integer) germplasmListTree.getSelectedListId();
		}
		
		GermplasmList folder = null;
		if(folderId != null){
			try {
				folder = germplasmListManager.getGermplasmListById(folderId);
			} catch (MiddlewareQueryException e) {
				LOG.error("Error with retrieving list with id: " + folderId, e);
			}
		}
		
		return folder;
	}
	
	public GermplasmList getGermplasmListToSave(){
		Integer currentId = null;
		if(germplasmList != null){
			currentId = germplasmList.getId();
		}
		
		GermplasmList selectedList = getSelectedListOnTree();
		
		//If selected item on list/folder tree is a list, use that as target germplasm list
		if(selectedList!=null && !FOLDER_TYPE.equalsIgnoreCase(selectedList.getType())){
			germplasmList = getSelectedListOnTree();
			
			//Needed for overwriting
			source.setCurrentlySavedGermplasmList(germplasmList);
			
			//If selected item is a folder, get parent of that folder
			try {
				selectedList = germplasmListManager.getGermplasmListById(selectedList.getParentId());
			} catch (MiddlewareQueryException e) {
				LOG.error("Error with getting parent list: " + selectedList.getParentId(), e);
			}
			
		//If not, use old method, get germplasm list the old way
		} else {
			germplasmList = listDetailsComponent.getGermplasmList();
			germplasmList.setId(currentId);
			germplasmList.setStatus(LIST_NAMES_STATUS);
		}
		
		germplasmList.setParent(selectedList);         
        return germplasmList;
	}

	protected boolean validateAllFields() {
		
		if(!listDetailsComponent.validate()){
			return false;
		}
		
		return true;
	}
	
	public BreedingManagerListDetailsComponent getDetailsComponent(){
		return this.listDetailsComponent;
	}
	
	public SaveListAsDialogSource getSource(){
		return source;
	}
	
	public void setGermplasmList(GermplasmList germplasmList){
		this.germplasmList = germplasmList;
	}
	
	public BreedingManagerListDetailsComponent getListDetailsComponent(){
		return listDetailsComponent;
	}
	
	public LocalListFoldersTreeComponent getGermplasmListTree(){
		return germplasmListTree;
	}
	
	public void saveReservationChanges(){
		if(source instanceof ListBuilderComponent){
			((ListBuilderComponent) source).saveReservationChangesAction();
		}
	}

	protected boolean isShowFoldersOnlyInListTree() {
		return showFoldersOnlyInListTree;
	}

	protected void setShowFoldersOnlyInListTree(boolean showFoldersOnlyInListTree) {
		this.showFoldersOnlyInListTree = showFoldersOnlyInListTree;
	}

	public GermplasmList getOriginalGermplasmList() {
		return originalGermplasmList;
	}

	public void setOriginalGermplasmList(GermplasmList originalGermplasmList) {
		this.originalGermplasmList = originalGermplasmList;
	}

	private void doSaveAction(final ClickEvent event) {
		//Call method so that the variables will be updated, values will be used for the logic below
		germplasmList = getGermplasmListToSave();
		
		if(isListDateValid(listDetailsComponent.getListDateField())){
			//If target list is locked
			if(isSelectedListLocked()) {
				MessageNotifier.showError(getWindow().getParent().getWindow(), 
						messageSource.getMessage(Message.ERROR), messageSource.getMessage(Message.UNABLE_TO_EDIT_LOCKED_LIST));
			
			//If target list to be overwritten is not itself and is an existing list
			} else if(isSelectedListAnExistingListButNotItself()) {
				
				final GermplasmList gl = getGermplasmListToSave();
				setGermplasmListDetails(gl);
				
			    ConfirmDialog.show(getWindow().getParent().getWindow(), messageSource.getMessage(Message.DO_YOU_WANT_TO_OVERWRITE_THIS_LIST)+"?", 
			            messageSource.getMessage(Message.LIST_DATA_WILL_BE_DELETED_AND_WILL_BE_REPLACED_WITH_THE_DATA_FROM_THE_LIST_THAT_YOU_JUST_CREATED), 
			            messageSource.getMessage(Message.OK), messageSource.getMessage(Message.CANCEL), 
			            new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;
							public void onClose(ConfirmDialog dialog) {
			                    if (dialog.isConfirmed()) {
									source.saveList(gl);
									saveReservationChanges();
									Window window = event.getButton().getWindow();
							        window.getParent().removeWindow(window);
			                    }
			                }
			            }
			        );
			    
			//If target list to be overwritten is itself
			} else {
				if(validateAllFields()){
					
					GermplasmList gl = getGermplasmListToSave();
					setGermplasmListDetails(gl);
					
					source.saveList(gl);
					saveReservationChanges();
					
					Window window = event.getButton().getWindow();
			        window.getParent().removeWindow(window);
				}
			}
			
			updateInventoryColumnsOnListDataAndListInventoryTables();
		} 
	}

	private void updateInventoryColumnsOnListDataAndListInventoryTables() {
		if(source instanceof ReserveInventorySource){
			ReserveInventoryAction reserveInventoryAction = new ReserveInventoryAction((ReserveInventorySource) source);
			if(source instanceof ParentTabComponent){
				boolean success = reserveInventoryAction.saveReserveTransactions(((ParentTabComponent) source).getValidReservationsToSave(), germplasmList.getId());
				if(success){
					((ParentTabComponent)source).refreshInventoryColumns(((ParentTabComponent)source).getValidReservationsToSave());
					((ParentTabComponent)source).resetListInventoryTableValues();
				}
			}
		}
	}

	private void setGermplasmListDetails(final GermplasmList gl) {
		gl.setName(listDetailsComponent.getListNameField().getValue().toString());
		gl.setDescription(listDetailsComponent.getListDescriptionField().getValue().toString());
		gl.setType(listDetailsComponent.getListTypeField().getValue().toString());
		gl.setDate(getCurrentParsedListDate(listDetailsComponent.getListDateField().getValue().toString()));
		gl.setNotes(listDetailsComponent.getListNotesField().getValue().toString());
	}

	protected boolean isSelectedListAnExistingListButNotItself() {
		return isSelectedListAnExistingList() || isSelectedListNotSameWithTheOriginalList();
	}

	protected boolean isSelectedListNotSameWithTheOriginalList() {
		return germplasmList.getId()!=null && originalGermplasmList!=null 
		&& germplasmList.getId() != originalGermplasmList.getId();
	}

	protected boolean isSelectedListAnExistingList() {
		return germplasmList.getType()!=null && !FOLDER_TYPE.equalsIgnoreCase(germplasmList.getType())
				&& (germplasmList.getId()!=null && originalGermplasmList==null);
	}

	protected boolean isSelectedListLocked() {
		return germplasmList!=null && germplasmList.getStatus()>=100;
	}

	private boolean isListDateValid(ListDateField listDateField) {
		
		try {
			listDateField.validate();
		} catch (InvalidValueException e) {
			LOG.error(e.getMessage(),e);
			MessageNotifier.showRequiredFieldError(getWindow().getParent().getWindow(), e.getMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * Parse the date value return from a DateField object to this format yyyymmdd
	 * @param listDate string with format: E MMM dd HH:mm:ss Z yyyy
	 * 		  If doesn't follow the format, will return the current date
	 * @return
	 */
	protected Long getCurrentParsedListDate(String listDate) {
		Date date;
		try {
			date = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(listDate);
		} catch (ParseException e) {
			date = new Date();
			LOG.error(e.getMessage(),e);
		}
		
		SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT);
		
		return Long.parseLong(formatter.format(date));
	}
}
