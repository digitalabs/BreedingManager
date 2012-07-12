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

package org.generationcp.browser.germplasm;

import java.util.Arrays;
import java.util.List;

import org.generationcp.browser.application.Message;
import org.generationcp.commons.spring.InternationalizableComponent;
import org.generationcp.commons.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Configurable
public class GermplasmSearchFormComponent extends VerticalLayout implements Property.ValueChangeListener, InitializingBean, InternationalizableComponent {

    private String choice;
    private String searchValue;
    private String databaseInstance;
    private final TextField txtSearchValue = new TextField();
    private OptionGroup searchSelect;
    private OptionGroup databaseInstanceOption;
    private static final List<String> SEARCH_OPTION = Arrays.asList(new String[] { "GID", "Names" });
    private static final List<String> INSTANCE_OPTION = Arrays.asList(new String[] { "Central", "Local" });
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public GermplasmSearchFormComponent() {

    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        // TODO Auto-generated method stub
        choice = searchSelect.getValue().toString();
        searchValue = txtSearchValue.getValue().toString();
        databaseInstance = databaseInstanceOption.getValue().toString();
        if (choice.equals("GID")) {
            databaseInstanceOption.setVisible(false);
        } else {
            databaseInstanceOption.setVisible(true);
        }
    }

    public String getChoice() {
        return choice;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public String getDatabaseInstance() {
        return databaseInstance;
    }
    
    
    @Override
    public void afterPropertiesSet() {
    	
        GridLayout grid = new GridLayout(4, 4);
        setSpacing(true);

        searchSelect = new OptionGroup("Search for", SEARCH_OPTION);
        searchSelect.select("Names");
        searchSelect.setImmediate(true);
        searchSelect.addListener(this);
        searchSelect.addStyleName("horizontal");
        grid.addComponent(searchSelect, 1, 1);

        txtSearchValue.addListener(this);
        txtSearchValue.setImmediate(true);
        txtSearchValue.addStyleName("addTopSpace");
        // txtSearchValue.setSizeUndefined();
        grid.addComponent(txtSearchValue, 2, 1);

        databaseInstanceOption = new OptionGroup("", INSTANCE_OPTION);
        databaseInstanceOption.select("Central");
        databaseInstanceOption.setImmediate(true);
        databaseInstanceOption.addListener(this);
        databaseInstanceOption.addStyleName("horizontal");

        grid.addComponent(databaseInstanceOption, 2, 2);
        addComponent(grid);

        this.choice = "Name";
        this.searchValue = "";
        this.databaseInstance = "Central";

    }
    
    @Override
    public void attach() {
    	
        super.attach();
        
        updateLabels();
    }
    

	@Override
	public void updateLabels() {

		messageSource.setCaption(searchSelect, Message.search_for_label);
        
	}

}
