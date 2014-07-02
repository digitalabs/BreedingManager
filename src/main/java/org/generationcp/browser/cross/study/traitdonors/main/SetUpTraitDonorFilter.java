package org.generationcp.browser.cross.study.traitdonors.main;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.browser.application.Message;
import org.generationcp.browser.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.pojos.CharacterTraitFilter;
import org.generationcp.browser.cross.study.adapted.main.pojos.NumericTraitFilter;
import org.generationcp.browser.cross.study.commons.trait.filter.CategoricalVariatesSection;
import org.generationcp.browser.cross.study.commons.trait.filter.CharacterTraitsSection;
import org.generationcp.browser.cross.study.commons.trait.filter.NumericTraitsSection;
import org.generationcp.browser.cross.study.h2h.main.pojos.EnvironmentForComparison;
import org.generationcp.browser.cross.study.traitdonors.main.listeners.TraitDonorButtonClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * Prompts the Breeder to select a range for each trait that they are interested in adaptation for the germplasm.
 * 
 * Traits are split into Numeric, Character and Categorial for processing
 * 
 * @author rebecca
 *
 */
@Configurable
public class SetUpTraitDonorFilter extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {
	
	public static final String NEXT_BUTTON_ID = "SetUpTraitFilter Next Button ID";
	   
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory.getLogger(SetUpTraitDonorFilter.class);

	private static final int NUM_OF_SECTIONS = 3;
	private static final Message[] tabLabels = {Message.NUMERIC_TRAITS, Message.CHARACTER_TRAIT_FILTER_TAB_TITLE, Message.CATEGORICAL_VARIATES};

	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	private TraitDonorsQueryMain mainScreen;
	private TraitDisplayResults nextScreen;
	private CharacterTraitsSection characterSection;
	private NumericTraitsSection numericSection;
	private CategoricalVariatesSection categoricalVariatesSection;

	private TabSheet mainTabSheet;
	private Button nextButton;
	
	private List<EnvironmentForComparison> environmentsForComparisonList;
	private List<Integer> selectedTraits;
	private List<Integer> environmentIds;
	
	
	public SetUpTraitDonorFilter(TraitDonorsQueryMain traitDonorsQueryMain, TraitDisplayResults nextScreen) {
		this.mainScreen = traitDonorsQueryMain;
		this.nextScreen = nextScreen;
	}

	@Override
	public void updateLabels() {
		if (nextButton != null){
			messageSource.setCaption(nextButton, Message.NEXT);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		setHeight("550px");
        setWidth("1000px");	 
	}
	
	/*
	 * Creates a three tabbed panel, one tab each to contain Numeric, Character and Categorical traits. These traits have been 
	 * pre-specified in the earlier PreselectTraitFiler panel.
	 * 
	 */
	public void createTraitsTabs() {		
		mainTabSheet = new TabSheet();
		mainTabSheet.setHeight("470px");
		
        for (int i = 0; i < NUM_OF_SECTIONS; i++){
        	VerticalLayout layout = new VerticalLayout();
        	
        	switch (i) {
				case 0:
					numericSection = new NumericTraitsSection(this.environmentIds, this.selectedTraits, this.getWindow());
					numericSection.showEmptyTraitsMessage();
					layout = numericSection;
					break;
				
				case 1:
					characterSection = new CharacterTraitsSection(this.environmentIds, this.selectedTraits, this.getWindow());
					layout = characterSection;
					break;
					
				case 2:
					categoricalVariatesSection = new CategoricalVariatesSection(this.environmentIds, this.selectedTraits, this.getWindow());
					layout = categoricalVariatesSection;
					break;
					
					
			}

        	
        	mainTabSheet.addTab(layout, messageSource.getMessage(tabLabels[i]));
        }
        
        mainTabSheet.addListener(new SelectedTabChangeListener() {
            private static final long serialVersionUID = -7294872922580572493L;

			@Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                Component selected = mainTabSheet.getSelectedTab();
                Tab tab = mainTabSheet.getTab(selected);

                if(tab!=null && tab.getCaption().equals(
                		messageSource.getMessage(tabLabels[0]))){
                	numericSection.showEmptyTraitsMessage();
                } else if(tab!=null && tab.getCaption().equals(
                		messageSource.getMessage(tabLabels[1]))){
                	characterSection.showEmptyTraitsMessage();
                } else if(tab!=null && tab.getCaption().equals(
                		messageSource.getMessage(tabLabels[2]))){
                	categoricalVariatesSection.showEmptyTraitsMessage();
                } 
            }
        });
        
        addComponent(mainTabSheet, "top:20px");
	}

	public void populateTraitsTables(List<EnvironmentForComparison> environments, List<Integer> traitsList) {
		this.environmentsForComparisonList = environments;
		this.selectedTraits = traitsList;
		this.environmentIds = new ArrayList<Integer>();
		for (EnvironmentForComparison envt : environments){
			this.environmentIds.add(envt.getEnvironmentNumber());
		}
		
		createTraitsTabs();	
		createButtonLayout();
	}
	
	
	private void createButtonLayout(){
		nextButton = new Button(messageSource.getMessage(Message.NEXT));
		nextButton.setWidth("80px");
		nextButton.setData(NEXT_BUTTON_ID);
		nextButton.addListener(new TraitDonorButtonClickListener(this));
		nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		addComponent(nextButton, "top:500px;left:900px");
		updateLabels();
	}
	
	// validate conditions before proceeding to next tab
	public void nextButtonClickAction(){
		if (numericSection != null){
			if (!numericSection.allFieldsValid()){
				return;
			}
			
			if (!categoricalVariatesSection.allFieldsValid()){
				return;
			}
		}
		
		// each Section looks after its own details for selection
		List<NumericTraitFilter> numericFilters = numericSection.getFilters();
		List<CharacterTraitFilter> characterFilters = characterSection.getFilters();
		List<CategoricalTraitFilter> categoricalFilters = categoricalVariatesSection.getFilters();
		
		// Do not allow user to proceed if all traits dropped
		if (numericSection.allTraitsDropped() && characterSection.allTraitsDropped() 
				&& categoricalVariatesSection.allTraitsDropped()){
			MessageNotifier.showWarning(getWindow(), messageSource.getMessage(Message.WARNING), 
					messageSource.getMessage(Message.ALL_TRAITS_DROPPED_WARNING));
			
		} else {
			this.mainScreen.selectFourthTab();
			this.nextScreen.populateResultsTable(environmentsForComparisonList,numericFilters,characterFilters,categoricalFilters);
		}
	}
	

	
	@Override
	public void attach() {
		super.attach();
		updateLabels();
	}
	
	


}