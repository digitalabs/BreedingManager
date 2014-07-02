package org.generationcp.browser.cross.study.h2h.main.listeners;

import org.generationcp.browser.cross.study.commons.EnvironmentFilter;
import org.generationcp.browser.cross.study.h2h.main.ResultsComponent;
import org.generationcp.browser.cross.study.h2h.main.SpecifyGermplasmsComponent;
import org.generationcp.browser.cross.study.h2h.main.TraitsAvailableComponent;
import org.generationcp.browser.cross.study.h2h.main.dialogs.AddEnvironmentalConditionsDialog;
import org.generationcp.browser.cross.study.h2h.main.dialogs.FilterLocationDialog;
import org.generationcp.browser.cross.study.h2h.main.dialogs.FilterStudyDialog;
import org.generationcp.browser.cross.study.h2h.main.dialogs.SelectGermplasmEntryDialog;
import org.generationcp.browser.cross.study.h2h.main.dialogs.SelectGermplasmListDialog;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;

public class HeadToHeadCrossStudyMainButtonClickListener implements Button.ClickListener {

    private static final long serialVersionUID = -3422805642974069212L;

    private static final Logger LOG = LoggerFactory.getLogger(HeadToHeadCrossStudyMainButtonClickListener.class);

    private Component source;
    private Integer studyId;
    private Component parentOfSource; // EnvironmentsAvailableCompoent or SpecifyAndWeighEnvironment
    
    public HeadToHeadCrossStudyMainButtonClickListener(Component source){
        this.source = source;
    }
    
    public HeadToHeadCrossStudyMainButtonClickListener(Component source, String countryName){
        this.source = source;
    }
    
    public HeadToHeadCrossStudyMainButtonClickListener(Component source, String countryName, String provinceName){
        this.source = source;
    }
    
    public HeadToHeadCrossStudyMainButtonClickListener(Component source, String countryName, String provinceName, Integer studyId){
        this.source = source;
        this.studyId = studyId;
    }
    
    public HeadToHeadCrossStudyMainButtonClickListener(Component source, Component parentOfSource){
        this.source = source;
        this.parentOfSource = parentOfSource;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton().getData().equals(SpecifyGermplasmsComponent.SELECT_TEST_SEARCH_GERMPLASM_BUTTON_ID)
                && (source instanceof SpecifyGermplasmsComponent)){
            try {
                ((SpecifyGermplasmsComponent) source).selectTestEntryButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(SpecifyGermplasmsComponent.SELECT_STANDARD_SEARCH_GERMPLASM_BUTTON_ID)
                && (source instanceof SpecifyGermplasmsComponent)){
            try {
                ((SpecifyGermplasmsComponent) source).selectStandardEntryButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(SpecifyGermplasmsComponent.SELECT_TEST_SEARCH_GERMPLASM_LIST_BUTTON_ID)
                && (source instanceof SpecifyGermplasmsComponent)){
            try {
                ((SpecifyGermplasmsComponent) source).selectTestGermplasmListButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(SpecifyGermplasmsComponent.SELECT_STANDARD_SEARCH_GERMPLASM_LIST_BUTTON_ID)
                && (source instanceof SpecifyGermplasmsComponent)){
            try {
                ((SpecifyGermplasmsComponent) source).selectStandardGermplasmListButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(SpecifyGermplasmsComponent.NEXT_BUTTON_ID)
                && (source instanceof SpecifyGermplasmsComponent)){
            try {
                ((SpecifyGermplasmsComponent) source).nextButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(TraitsAvailableComponent.BACK_BUTTON_ID)
                && (source instanceof TraitsAvailableComponent)){
            try {
                ((TraitsAvailableComponent) source).backButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(TraitsAvailableComponent.NEXT_BUTTON_ID)
                && (source instanceof TraitsAvailableComponent)){
            try {
                ((TraitsAvailableComponent) source).nextButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(ResultsComponent.EXPORT_BUTTON_ID)
                && (source instanceof ResultsComponent)){
            try {
                ((ResultsComponent) source).exportButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        }else if (event.getButton().getData().equals(ResultsComponent.BACK_BUTTON_ID)
                && (source instanceof ResultsComponent)){
            try {
                ((ResultsComponent) source).backButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (event.getButton().getData().equals(SelectGermplasmEntryDialog.ADD_BUTTON_ID)
                && (source instanceof SelectGermplasmEntryDialog)){
            try {
                ((SelectGermplasmEntryDialog) source).addButtonClickAction();
            } catch (InternationalizableException e){
                LOG.error(e.toString() + "\n" + e.getStackTrace());
                e.printStackTrace();
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
            }
        } else if (source instanceof SelectGermplasmEntryDialog
                && event.getButton().getData().equals(SelectGermplasmEntryDialog.SEARCH_BUTTON_ID)) {
            ((SelectGermplasmEntryDialog) source).searchButtonClickAction();
        } else if (source instanceof SelectGermplasmListDialog
                && event.getButton().getData().equals(SelectGermplasmListDialog.ADD_BUTTON_ID)) {
            ((SelectGermplasmListDialog) source).populateParentList();
        } 
        
        //Common in Adapted Germplasm and H2H
        else if (source instanceof FilterLocationDialog
                && event.getButton().getData().equals(FilterLocationDialog.APPLY_BUTTON_ID)) {
        	
        	((FilterLocationDialog) source).clickApplyButton(getParentClass(parentOfSource));
            
        } else if (source instanceof FilterStudyDialog
                && event.getButton().getData().equals(FilterStudyDialog.APPLY_BUTTON_ID)) {
        	
        	((FilterStudyDialog) source).clickApplyButton(getParentClass(parentOfSource));
        	
        } else if (source instanceof FilterStudyDialog
                && event.getButton().getData().equals(FilterStudyDialog.STUDY_BUTTON_ID)) {
            ((FilterStudyDialog) source).showStudyInfo(studyId);
        } else if (source instanceof AddEnvironmentalConditionsDialog
                && event.getButton().getData().equals(AddEnvironmentalConditionsDialog.APPLY_BUTTON_ID)) {
        	
            ((AddEnvironmentalConditionsDialog) source).clickApplyButton(getParentClass(parentOfSource));
        }
        else {
            LOG.error("HeadToHeadCrossStudyMainButtonClickListener: Error with buttonClick action. Source not identified.");
        }
    }
    
    public String getParentClass(Component parentOfSource){
    	String parentClass = "";
    	
    	if( parentOfSource instanceof EnvironmentFilter ){
    		parentClass = "EnvironmentFilter";
    	}
    	
    	return parentClass;
    }

}