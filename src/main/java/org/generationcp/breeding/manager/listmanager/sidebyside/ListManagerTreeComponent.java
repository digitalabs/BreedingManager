package org.generationcp.breeding.manager.listmanager.sidebyside;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.customfields.ListTreeComponent;
import org.generationcp.breeding.manager.listeners.ListTreeActionsListener;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ListManagerTreeComponent extends ListTreeComponent implements InitializingBean{

	private static final long serialVersionUID = -1013380483927558222L;

	
	public ListManagerTreeComponent(ListTreeActionsListener treeActionsListener) {
		super(treeActionsListener);
	}
	
	public ListManagerTreeComponent(ListTreeActionsListener treeActionListener, Integer listId){
		super(treeActionListener, listId);
	}


	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Override
	protected boolean doIncludeActionsButtons() {
		return true;
	}

	@Override
	protected String getTreeHeading() {
		return messageSource.getMessage(Message.LISTS);
	}

	@Override
	protected boolean doIncludeRefreshButton() {
		return true;
	}

	@Override
	protected boolean isTreeItemsDraggable() {
		return true;
	}

	@Override
	protected boolean doIncludeCentralLists() {
		return true;
	}

	@Override
	protected boolean doShowFoldersOnly() {
		return false;
	}

	@Override
	protected boolean doIncludeToogleButton() {
		return true;
	}

	@Override
	protected void toogleListTreePane() {
		this.treeActionsListener.toggleListTreeComponent();
	}
	
}
