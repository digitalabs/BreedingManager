package org.generationcp.breeding.manager.crossingmanager.settings;

import java.util.List;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.crossingmanager.xml.CrossingManagerSetting;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.TemplateSetting;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class DefineCrossingSettingComponent extends VerticalLayout implements BreedingManagerLayout,
		InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 8015092540102625727L;
	private static final Logger LOG = LoggerFactory.getLogger(DefineCrossingSettingComponent.class);
	
	public enum UsePreviousSettingOption {
		YES, NO
	}
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	
	private CrossingSettingsDetailComponent settingsParentComponent;
	
	private Label mandatoryFieldLabel;
	private Label usePreviouslySavedSettingLabel;
	
	private OptionGroup usePreviousSettingOptionGroup;
	private ComboBox settingsComboBox;
	private Button deleteSettingButton;
	
	public DefineCrossingSettingComponent(CrossingSettingsDetailComponent settingsParentComponent){
		this.settingsParentComponent = settingsParentComponent;
	}
	
	@Override
	public void attach() {
		super.attach();
		updateLabels();
	}
	
	@Override
	public void updateLabels() {
		usePreviouslySavedSettingLabel.setValue(messageSource.getMessage(Message.USE_PREVIOUSLY_SAVED_SETTING));
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
		
		mandatoryFieldLabel =  new Label("<i>" +messageSource.getMessage(Message.INDICATES_A_MANDATORY_FIELD) 
				+ "</i>", Label.CONTENT_XHTML);
		
		usePreviouslySavedSettingLabel = new Label();
		usePreviouslySavedSettingLabel.addStyleName(AppConstants.CssStyles.BOLD);
		usePreviouslySavedSettingLabel.setWidth("210px");
		
		usePreviousSettingOptionGroup = new OptionGroup();
		usePreviousSettingOptionGroup.setImmediate(true);
		usePreviousSettingOptionGroup.setWidth("100px");
		usePreviousSettingOptionGroup.addStyleName(AppConstants.CssStyles.HORIZONTAL_GROUP);
		
		settingsComboBox = new ComboBox();
		settingsComboBox.setWidth("260px");
		settingsComboBox.setImmediate(true);
		settingsComboBox.setNullSelectionAllowed(true);
		settingsComboBox.setTextInputAllowed(false);
			
		deleteSettingButton = new Button("<span class='glyphicon glyphicon-trash' style='left: 2px; color: #7c7c7c;font-size: 16px; font-weight: bold;'></span>");
		deleteSettingButton.setHtmlContentAllowed(true);
		deleteSettingButton.setDescription("Delete Setting");
		deleteSettingButton.setStyleName(Reindeer.BUTTON_LINK);
		deleteSettingButton.setWidth("25px");
	}

	@Override
	public void initializeValues() {
		usePreviousSettingOptionGroup.addItem(UsePreviousSettingOption.NO);
		usePreviousSettingOptionGroup.setItemCaption(UsePreviousSettingOption.NO, messageSource.getMessage(Message.NO));
		usePreviousSettingOptionGroup.addItem(UsePreviousSettingOption.YES);
		usePreviousSettingOptionGroup.setItemCaption(UsePreviousSettingOption.YES, messageSource.getMessage(Message.YES));
		usePreviousSettingOptionGroup.select(UsePreviousSettingOption.NO);
		
		settingsComboBox.setInputPrompt(messageSource.getMessage(Message.CHOOSE_SAVED_SETTINGS));
		setSettingsComboBox(null);
		toggleSettingsFields(false);
	}

	@Override
	public void addListeners() {
		// enable / disable settings combobox
		usePreviousSettingOptionGroup.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean doUsePreviousSetting = UsePreviousSettingOption.YES.equals(
						usePreviousSettingOptionGroup.getValue());
				toggleSettingsFields(doUsePreviousSetting);
				if (!doUsePreviousSetting){
					revertScreenToDefaultValues();
				}
			}
		});
		
		settingsComboBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(settingsComboBox.getValue() != null){
					settingsParentComponent.setCurrentSetting(getSelectedTemplateSetting());
					settingsParentComponent.setManageCrossingSettingsFields();
				}
				else{
					revertScreenToDefaultValues();
				}
			}

		});
		
		deleteSettingButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -432280582291837428L;

			@Override
			public void buttonClick(ClickEvent event) {
				settingsParentComponent.doDeleteAction();
			}
		});
	}

	@Override
	public void layoutComponents() {
		
		addComponent(mandatoryFieldLabel);
		
		HorizontalLayout previousSettingLayout = new HorizontalLayout();
		previousSettingLayout.setSpacing(true);
		previousSettingLayout.addComponent(usePreviouslySavedSettingLabel);
		previousSettingLayout.addComponent(usePreviousSettingOptionGroup);
		previousSettingLayout.addComponent(settingsComboBox);
		previousSettingLayout.addComponent(deleteSettingButton);
		
		addComponent(previousSettingLayout);
	}
	
	public void setSettingsComboBox(TemplateSetting currentSetting){
		settingsComboBox.removeAllItems();
		try {
			Tool crossingManagerTool = workbenchDataManager.getToolWithName(CrossingManagerSetting.CROSSING_MANAGER_TOOL_NAME);
			
			TemplateSetting templateSettingFilter = new TemplateSetting();
			templateSettingFilter.setTool(crossingManagerTool);
			
			List<TemplateSetting> templateSettings = workbenchDataManager.getTemplateSettings(templateSettingFilter);
			
			for(TemplateSetting ts : templateSettings){
				settingsComboBox.addItem(ts);
				settingsComboBox.setItemCaption(ts, ts.getName());
			}
			
			if(currentSetting != null){
				usePreviousSettingOptionGroup.select(UsePreviousSettingOption.YES);
				settingsComboBox.select(currentSetting);
			}
			
		} catch (MiddlewareQueryException e) {
			//commenting out code for showing error notification because at this point this component is not yet attached to a window and so getWindow() returns null
			//MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR), "Error getting crossing templates!");
			LOG.error("Error with retrieving Workbench template settings for Crossing Manager tool.", e);
		}
	}
	
	public TemplateSetting getSelectedTemplateSetting(){
		return (TemplateSetting) settingsComboBox.getValue();
	}
	
	private void revertScreenToDefaultValues() {
		settingsParentComponent.setCurrentSetting(null);
		settingsParentComponent.setDefaultManageCrossingSettingsFields();
	}
	
	private void toggleSettingsFields(boolean enabled){
		settingsComboBox.setEnabled(enabled);
		deleteSettingButton.setEnabled(enabled);
	}

}
