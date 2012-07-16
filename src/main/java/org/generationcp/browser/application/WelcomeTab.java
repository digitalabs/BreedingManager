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

package org.generationcp.browser.application;

import org.generationcp.browser.germplasm.GermplasmBrowserMain;
import org.generationcp.browser.germplasm.GidByPhenotypicQueries;
import org.generationcp.browser.germplasm.SearchGermplasmByPhenotypicTab;
import org.generationcp.browser.germplasm.TraitDataIndexContainer;
import org.generationcp.browser.germplasm.listeners.GermplasmButtonClickListener;
import org.generationcp.browser.study.StudyBrowserMain;
import org.generationcp.browser.study.listeners.StudyButtonClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * This class extends a VerticalLayout and is basically a container for the
 * components to be shown on the Welcome tab of the application. From the
 * Welcome tab, you can access the other tabs of the main application window
 * thru the use of the buttons.
 * 
 * @author Kevin Manansala, Jeffrey Morales
 * 
 */
@Configurable
public class WelcomeTab extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private final static Logger LOG = LoggerFactory.getLogger(WelcomeTab.class);
    private static final long serialVersionUID = -917787404988386915L;
    
    public static final String BROWSE_STUDY_BUTTON_ID = "WelcomeTab Browse Study Button";
    public static final String BROWSE_GERMPLASM_BUTTON_ID = "WelcomeTab Browse Germplasm Button";
    public static final String BROWSE_GERMPLASM_BY_PHENO_BUTTON_ID = "WelcomeTab Browse Germplasm By Pheno Button";
    
    @SuppressWarnings("unused")
    private int screenWidth;

    VerticalLayout rootLayoutsForOtherTabs[];
    
    private VerticalLayout rootLayoutForGermplasmBrowser;

    private VerticalLayout rootLayoutForStudyBrowser;
    private TabSheet theTabSheet;

    private VerticalLayout rootLayoutForGermplasmByPheno;
    private GidByPhenotypicQueries gidByPhenoQueries;
    private TraitDataIndexContainer traitDataCon;

    private Label welcomeLabel;
    private Label questionLabel;
    private Button germplasmButton;
    private Button studyButton;
    private Button germplasmByPhenoButton;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public WelcomeTab(TabSheet tabSheet, VerticalLayout rootLayoutsForOtherTabs[]) {
 
        this.theTabSheet = tabSheet;
        this.rootLayoutsForOtherTabs = rootLayoutsForOtherTabs;
        this.gidByPhenoQueries = new GidByPhenotypicQueries();
        this.traitDataCon = new TraitDataIndexContainer();

    }

    // Called by GermplasmButtonClickListener
    public void browserGermplasmInfoButtonClickAction() {
        if (rootLayoutForGermplasmBrowser.getComponentCount() == 0) {
            rootLayoutForGermplasmBrowser.addComponent(new GermplasmBrowserMain());
            rootLayoutForGermplasmBrowser.addStyleName("addSpacing");
        }

        theTabSheet.setSelectedTab(rootLayoutForGermplasmBrowser);
    }

    // Called by StudyButtonClickListener
    public void browseStudiesAndDataSets() {
        if (rootLayoutForStudyBrowser.getComponentCount() == 0) {
            rootLayoutForStudyBrowser.setWidth("100%");
            rootLayoutForStudyBrowser.addComponent(new StudyBrowserMain());
            rootLayoutForStudyBrowser.addStyleName("addSpacing");
        }

        theTabSheet.setSelectedTab(rootLayoutForStudyBrowser);

    }

    // Called by GermplasmButtonClickListener
    public void searchGermplasmByPhenotyicDataButtonClickAction() {
        // when the button is clicked, content for the tab is
        // dynamically created
        // creation of content is done only once
        if (rootLayoutForGermplasmByPheno.getComponentCount() == 0) {
            try {
                rootLayoutForGermplasmByPheno.addComponent(new SearchGermplasmByPhenotypicTab(gidByPhenoQueries, traitDataCon));
            } catch (QueryException e) {
                // Log into log file
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
            }
        }
        theTabSheet.setSelectedTab(rootLayoutForGermplasmByPheno);

    }

	@Override
	public void afterPropertiesSet() throws Exception {
        
        this.setSpacing(true);
        this.setMargin(true);

        welcomeLabel = new Label(); // "<h1>Welcome to the Germplasm and Study Browser</h1>"
        //welcomeLabel.setContentMode(Label.CONTENT_XHTML);
        welcomeLabel.setStyleName("h1");
        this.addComponent(welcomeLabel);

        questionLabel = new Label(); // "<h3>What do you want to do?</h3>"
        //questionLabel.setContentMode(Label.CONTENT_XHTML);
        questionLabel.setStyleName("h3");
        this.addComponent(questionLabel);

        germplasmButton = new Button(); // "I want to browse Germplasm information"
        germplasmButton.setWidth(400, UNITS_PIXELS);
        germplasmButton.setData(BROWSE_GERMPLASM_BUTTON_ID);
        this.rootLayoutForGermplasmBrowser = rootLayoutsForOtherTabs[0];

        germplasmButton.addListener(new GermplasmButtonClickListener(this));
        this.addComponent(germplasmButton);

        studyButton = new Button(); // "I want to browse Studies and their Datasets"
        studyButton.setWidth(400, UNITS_PIXELS);
        studyButton.setData(BROWSE_STUDY_BUTTON_ID);
        rootLayoutForStudyBrowser = rootLayoutsForOtherTabs[1];

        studyButton.addListener(new StudyButtonClickListener(this));

        this.addComponent(studyButton);

        germplasmByPhenoButton = new Button(); // "I want to retrieve Germplasms by Phenotypic Data"
        germplasmByPhenoButton.setWidth(400, UNITS_PIXELS);
        germplasmByPhenoButton.setData(BROWSE_GERMPLASM_BY_PHENO_BUTTON_ID);
        rootLayoutForGermplasmByPheno = rootLayoutsForOtherTabs[2];

        germplasmByPhenoButton.addListener(new GermplasmButtonClickListener(this));
        this.addComponent(germplasmByPhenoButton);
		
	}
	
    @Override
    public void attach() {
    	
        super.attach();
        
        updateLabels();
    }
    

	@Override
	public void updateLabels() {
		
		messageSource.setCaption(welcomeLabel, Message.welcome_label);
		messageSource.setCaption(questionLabel, Message.question_label);
		messageSource.setCaption(germplasmButton, Message.germplasm_button_label);
		messageSource.setCaption(studyButton, Message.study_button_label);
		messageSource.setCaption(germplasmByPhenoButton, Message.germplasms_by_pheno_label);
		
	}
	
	
}
