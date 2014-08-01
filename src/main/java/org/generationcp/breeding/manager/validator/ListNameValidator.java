package org.generationcp.breeding.manager.validator;

import java.util.List;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.customfields.ListTreeComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator;

@Configurable
public class ListNameValidator implements Validator {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = LoggerFactory.getLogger(ListNameValidator.class);
	
	private static final String DEFAULT_ERROR = "Please specify the name and/or location of the list";
	private static final String SAME_PARENT_FOLDER_LIST_NAME_ERROR = "List Name and its Parent Folder must not have the same name";
	
	private String errorDetails;
	private String parentFolder;
	
	private String currentListName;
	
	@Autowired
    private GermplasmListManager germplasmListManager;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	public ListNameValidator(){
	}
	
	public ListNameValidator(String parentFolder){
		this.parentFolder = parentFolder;
		this.errorDetails = DEFAULT_ERROR;
	}
	
	public ListNameValidator(String parentFolder, String currentListName){
		this.parentFolder = parentFolder;
		this.errorDetails = DEFAULT_ERROR;
		this.currentListName = currentListName;
	}
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		
		if (!isValid(value)){
			
			throw new InvalidValueException(this.errorDetails);
		}
	}

	@Override
	public boolean isValid(Object value) {
		if(parentFolder != null){
			
	    	if(parentFolder.trim().length() == 0){
				this.errorDetails = DEFAULT_ERROR;
				return false;
			}
	    	
			if(parentFolder.trim().endsWith(value.toString() + " >")){	
				this.errorDetails = SAME_PARENT_FOLDER_LIST_NAME_ERROR;
				return false;
			}
		}

		if(!validateListName(value.toString())){
			return false;
		}
		
		return true;
	}
	
	private boolean validateListName(String listName){
		listName = listName.trim();
		if (listName.isEmpty()){
			this.errorDetails = messageSource.getMessage(Message.INVALID_ITEM_NAME);
			return false;
			
		} else if (ListTreeComponent.PROGRAM_LISTS.equalsIgnoreCase(listName) || 
				ListTreeComponent.PUBLIC_LISTS.equalsIgnoreCase(listName) ){
			this.errorDetails = "Cannot use \"Program Lists\" and \"Public Lists\" as item name.";
			return false;
		}
		
		try{
			List<GermplasmList> centralLists = this.germplasmListManager.getGermplasmListByName(listName, 0, 5, Operation.EQUAL, Database.CENTRAL);
			
			if(!centralLists.isEmpty()){
				this.errorDetails = messageSource.getMessage(Message.EXISTING_LIST_IN_CENTRAL_ERROR_MESSAGE);
				return false;
			}
			
			List<GermplasmList> localLists = this.germplasmListManager.getGermplasmListByName(listName, 0, 5, Operation.EQUAL, Database.LOCAL);
			
			if(localLists.size() == 1){
				if(!localLists.get(0).getName().trim().equals(currentListName)){
					this.errorDetails = messageSource.getMessage(Message.EXISTING_LIST_ERROR_MESSAGE);
					return false;
				}
			}
			else if(!localLists.isEmpty()){
				this.errorDetails = messageSource.getMessage(Message.EXISTING_LIST_ERROR_MESSAGE);
				return false;
			}
			
		} catch(MiddlewareQueryException ex){
			LOG.error("Error with getting germplasm list by list name - " + listName, ex);
			this.errorDetails = messageSource.getMessage(Message.ERROR_VALIDATING_LIST);
			return false;
		}
		
		return true;
	}

	public String getCurrentListName() {
		return currentListName;
	}

	public void setCurrentListName(String currentListName) {
		this.currentListName = currentListName;
	}

	public String getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(String parentFolder) {
		this.parentFolder = parentFolder;
	}
}
