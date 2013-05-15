package org.generationcp.breeding.manager.crossingmanager;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.crossingmanager.pojos.CrossesMade;
import org.generationcp.breeding.manager.pojos.ImportedGermplasmCrosses;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class CrossingManagerMain extends VerticalLayout implements InitializingBean, InternationalizableComponent{
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";
	
	
    private static final long serialVersionUID = -6656072296236475385L;

    private final static Logger LOG = LoggerFactory.getLogger(CrossingManagerMain.class);
    
    private final static String VERSION = "1.1.1.0";
    
    private CrossingManagerImportFileComponent wizardScreenOne;
    private CrossingManagerMakeCrossesComponent wizardScreenTwo;
    private CrossingManagerAdditionalDetailsComponent wizardScreenThree;
    private CrossingManagerDetailsComponent wizardScreenFour;
    
    private Tab wizardTabOne;
    private Tab wizardTabTwo;
    private Tab wizardTabThree;
    private Tab wizardTabFour;
    
    private Label crossingManagerTitle;
    private Accordion accordion;
    
    //Data from wizard steps
    private ImportedGermplasmCrosses importedGermplasmCrosses;
    private CrossesMade crossesMade;
    
    private ComponentContainer parent;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public CrossingManagerMain(ComponentContainer parent){
    	this.parent = parent;
    }

    @SuppressWarnings("serial")
	@Override
    public void afterPropertiesSet() throws Exception {
        setMargin(false);
        setSpacing(true);
        
        crossingManagerTitle = new Label("Crossing Manager " + VERSION);
        crossingManagerTitle.setStyleName("h1");
        addComponent(crossingManagerTitle);
        
        accordion = new Accordion();
        accordion.setWidth("800px");
        
        wizardScreenOne = new CrossingManagerImportFileComponent(this, accordion);
        wizardScreenTwo = new CrossingManagerMakeCrossesComponent(this, accordion);
        wizardScreenThree = new CrossingManagerAdditionalDetailsComponent(this, accordion);
        wizardScreenFour = new CrossingManagerDetailsComponent(this,accordion);
        
        wizardScreenOne.setNextScreen(wizardScreenTwo);
        wizardScreenOne.setNextNextScreen(wizardScreenThree);
        
        wizardScreenTwo.setNextScreen(wizardScreenThree);
        wizardScreenTwo.setPreviousScreen(wizardScreenOne);
        
        wizardScreenThree.setNextScreen(wizardScreenFour);
        
        wizardTabOne = accordion.addTab(wizardScreenOne, messageSource.getMessage(Message.SELECT_NURSERY_TEMPLATE)); //Select Nursery Template
        wizardTabTwo = accordion.addTab(wizardScreenTwo, messageSource.getMessage(Message.MAKE_CROSSES)); //Make crosses
        wizardTabThree = accordion.addTab(wizardScreenThree, messageSource.getMessage(Message.ENTER_ADDITIONAL_DETAILS_OF_GERMPLASM_RECORDS_FOR_CROSSES)); //Enter additional details of germplasm records for crosses
        wizardTabFour = accordion.addTab(wizardScreenFour, messageSource.getMessage(Message.ENTER_DETAILS_FOR_LIST_OF_CROSS)); //Enter details for list of cross
        
        accordion.addListener(new SelectedTabChangeListener() {
    	    @Override
    	    public void selectedTabChange(SelectedTabChangeEvent event) {
    	        Component selected =accordion.getSelectedTab();
    	        Tab tab = accordion.getTab(selected);
    	        
    	        //This part decides which tabs to enable/disable on change event. 
    	        //  All tabs are enabled everytime tab is changed, so all this has to do 
    	        //  is to disable the non-related tabs
    	        if(tab!=null && tab.equals(wizardTabOne)){
    	            enableOnlyWizardTabOne();
    	        } else if(tab!=null && tab.equals(wizardTabTwo)){    
    	            enableOnlyWizardTabTwo();
    	            enableWizardTabOne();
                } else if(tab!=null && tab.equals(wizardTabThree)){    
                    enableOnlyWizardTabThree();
                    wizardScreenThree.getCrossInfoComponent().populateHarvestLocation();
                } else if(tab!=null && tab.equals(wizardTabFour)){    
                    enableOnlyWizardTabFour();
                }
    	                    
    	    }
    	});
        addComponent(accordion);
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        
    }
    
    public CrossingManagerImportFileComponent getWizardScreenOne() {

    	return wizardScreenOne;
    }
    public CrossingManagerMakeCrossesComponent getWizardScreenTwo() {
    	return wizardScreenTwo;
    }
    public CrossingManagerAdditionalDetailsComponent getWizardScreenThree() {
    	return wizardScreenThree;
    }
    
    public CrossingManagerDetailsComponent getWizardScreenFour() {
    	return wizardScreenFour;
    }

    public void enableWizardTabs() {
        wizardTabOne.setEnabled(true);
        wizardTabTwo.setEnabled(true);
        wizardTabThree.setEnabled(true);
        wizardTabFour.setEnabled(true);
    }
    
    public void enableOnlyWizardTabOne() {
        wizardTabOne.setEnabled(true);
        wizardTabTwo.setEnabled(false);
        wizardTabThree.setEnabled(false);
        wizardTabFour.setEnabled(false);
    }
    
    public void enableOnlyWizardTabTwo() {
        wizardTabOne.setEnabled(false);
        wizardTabTwo.setEnabled(true);
        wizardTabThree.setEnabled(false);
        wizardTabFour.setEnabled(false);
    }
    
    public void enableOnlyWizardTabThree() {
        wizardTabOne.setEnabled(false);
        wizardTabTwo.setEnabled(false);
        wizardTabThree.setEnabled(true);
        wizardTabFour.setEnabled(false);
    }    
  
    public void enableOnlyWizardTabFour() {
        wizardTabOne.setEnabled(false);
        wizardTabTwo.setEnabled(false);
        wizardTabThree.setEnabled(false);
        wizardTabFour.setEnabled(true);
    }    

    public void enableWizardTabOne() {
        wizardTabOne.setEnabled(true);
    }
    
    public void enableWizardTabTwo() {
        wizardTabTwo.setEnabled(true);
    }
    
    public void enableWizardTabThree() {
        wizardTabThree.setEnabled(true);
    }    
  
    public void enableWizardTabFour() {
        wizardTabFour.setEnabled(true);
    }    

    
    public void setImportedGermplasmCrosses(ImportedGermplasmCrosses importedGermplasmCrosses){
        this.importedGermplasmCrosses = importedGermplasmCrosses;
    }
    
    public ImportedGermplasmCrosses getImportedGermplasmCrosses(){
        return importedGermplasmCrosses;
    }
    
    public void setCrossesMade(CrossesMade crossesMade){
        this.crossesMade = crossesMade;
    }
    public CrossesMade getCrossesMade(){
        return crossesMade;
    }
    
    public void viewGermplasmListCreated(Integer listId){
		EmbeddedGermplasmListDetailComponent germplasmListBrowser = 
		    new EmbeddedGermplasmListDetailComponent(this, listId);
		
		this.removeComponent(this.accordion);
		this.addComponent(germplasmListBrowser);
    }
    
    public void reset(){
    	this.parent.replaceComponent(this, new CrossingManagerMain(this.parent));
    }
}
