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

package org.generationcp.browser.study;

import org.generationcp.browser.application.Message;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.manager.Database;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class StudyBrowserMain extends VerticalLayout implements InitializingBean, InternationalizableComponent  {

    private static final long serialVersionUID = 1L;
    
    private final static String VERSION = "1.2.0";
    
    //private VerticalLayout tabLocalInstance;
    //private VerticalLayout tabCentralInstance;
    private VerticalLayout tabStudies;
    private VerticalLayout tabSearch;
    private TabSheet tabSheetStudyDatabaseInstance;
    
    //private StudyTreeComponent centralStudyTree;
    //private StudyTreeComponent localStudyTree;
    private StudyTreeComponent combinedStudyTree;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public StudyBrowserMain() {
    }
    
    @Override
    public void afterPropertiesSet() {
        this.setSpacing(true);
        this.setMargin(false, true, true, true);
        
        String title =  "Study Browser  <h2>" + VERSION + "</h2>";
        Label applicationTitle = new Label();
        applicationTitle.setStyleName(Bootstrap.Typography.H1.styleName());
        applicationTitle.setContentMode(Label.CONTENT_XHTML);
        applicationTitle.setValue(title);
        this.addComponent(applicationTitle);
        
        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setSizeFull();
        mainLayout.setSpacing(true);
        
        tabSheetStudyDatabaseInstance = new TabSheet();
        tabSheetStudyDatabaseInstance.setWidth("100%");
        tabSheetStudyDatabaseInstance.setHeight("600px");

        //tabLocalInstance = new VerticalLayout();
        //tabCentralInstance = new VerticalLayout();
        tabStudies = new VerticalLayout();
        tabSearch = new VerticalLayout();

        //tabSheetStudyDatabaseInstance.addTab(tabLocalInstance).setCaption(messageSource.getMessage(Message.DB_LOCAL_TEXT)); // "Local"
        //tabSheetStudyDatabaseInstance.addTab(tabCentralInstance).setCaption(messageSource.getMessage(Message.DB_CENTRAL_TEXT)); // "Central"
        tabSheetStudyDatabaseInstance.addTab(tabStudies).setCaption(messageSource.getMessage(Message.STUDIES)); // "Combined Central and Local"
        tabSheetStudyDatabaseInstance.addTab(tabSearch).setCaption(messageSource.getMessage(Message.SEARCH_LABEL)); // "Search"
        tabSheetStudyDatabaseInstance.setSelectedTab(tabStudies);

        //centralStudyTree = new StudyTreeComponent(mainLayout, Database.CENTRAL);
        //localStudyTree = new StudyTreeComponent(mainLayout, Database.LOCAL);
        combinedStudyTree = new StudyTreeComponent(mainLayout);

        //tabCentralInstance.addComponent(centralStudyTree);
        //tabLocalInstance.addComponent(localStudyTree);
        tabStudies.addComponent(combinedStudyTree);
        tabSearch.addComponent(new StudySearchMainComponent(mainLayout));

        mainLayout.addComponent(tabSheetStudyDatabaseInstance);
        mainLayout.setExpandRatio(tabSheetStudyDatabaseInstance, 0.60f);
        
        this.addComponent(mainLayout);
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        //messageSource.setCaption(tabLocalInstance, Message.DB_LOCAL_TEXT);
        //messageSource.setCaption(tabCentralInstance, Message.DB_CENTRAL_TEXT);
        messageSource.setCaption(tabStudies, Message.STUDIES);
        messageSource.setCaption(tabSearch, Message.SEARCH_LABEL);
    }
    
    public void setSelectedTab(VerticalLayout layout){
    	tabSheetStudyDatabaseInstance.setSelectedTab(layout);
    }
    
    //public VerticalLayout getTabLocalInstance(){
    //	return tabLocalInstance;
    //}
    
    //public VerticalLayout getTabCentralInstance(){
    //	return tabCentralInstance;
    //}
    
    public VerticalLayout getTabStudies(){
    	return tabStudies;
    }
    
    //public StudyTreeComponent getCentralStudyTreeComponent(){
    //	return centralStudyTree;
    //}
    
    //public StudyTreeComponent getLocalStudyTreeComponent(){
    //	return localStudyTree;
    //}
    
    public StudyTreeComponent getCombinedStudyTreeComponent(){
    	return combinedStudyTree;
    }
}
