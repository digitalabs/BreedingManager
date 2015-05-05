package org.generationcp.breeding.manager.listimport;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.customfields.UploadField;
import org.generationcp.breeding.manager.listimport.exceptions.GermplasmImportException;
import org.generationcp.breeding.manager.listimport.listeners.GermplasmImportButtonClickListener;
import org.generationcp.breeding.manager.listimport.util.GermplasmListUploader;
import org.generationcp.commons.parsing.FileParsingException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Locale;

@Configurable
public class GermplasmImportFileComponent extends AbsoluteLayout implements InitializingBean, 
		InternationalizableComponent, BreedingManagerLayout {
    
    public  static final String FB_CLOSE_WINDOW_JS_CALL = "window.parent.cancelImportGermplasm()";
	private static final long serialVersionUID = 9097810121003895303L;
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmImportFileComponent.class);
    
    private GermplasmImportMain source;

    public static final String NEXT_BUTTON_ID = "next button";
    
    private Label selectFileLabel;
    private UploadField uploadComponents;
    private Button cancelButton;
    private Button nextButton;
    private Button openTemplateButton;
    private GermplasmListUploader germplasmListUploader;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public GermplasmImportFileComponent(GermplasmImportMain source){
        this.source = source;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
       instantiateComponents();
       initializeValues();
       addListeners();
       layoutComponents();
    }

    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(nextButton, Message.NEXT);
        messageSource.setCaption(openTemplateButton, Message.HERE);
    }
    
    public void nextButtonClickAction() {
    	try {
    		germplasmListUploader.doParseWorkbook();

            if ("".equals(germplasmListUploader.hasWarnings())) {
                MessageNotifier.showMessage(source.getWindow(), "Success", "File was successfully uploaded");
            } else {
                MessageNotifier.showWarning(source.getWindow(), "Warning",
                        germplasmListUploader.hasWarnings());
            }

    		source.nextStep();
			
		} catch (GermplasmImportException e) {
			LOG.debug("Error importing " + e.getMessage(), e);
			MessageNotifier.showError(getWindow(), e.getCaption(), e.getMessage());
		} catch (FileParsingException e) {
            LOG.debug("Error importing " + e.getMessage(), e);
            String message = messageSource.getMessage(e.getMessage(), e.getMessageParameters(), Locale
                    .getDefault());
            MessageNotifier.showError(getWindow(), "Error",message);
        }
    }
    
    public GermplasmImportMain getSource() {
        return source;
    }

    public void initializeUploadField(){
    	uploadComponents = new UploadField(){
			private static final long serialVersionUID = 1L;
			@Override
            public void uploadFinished(Upload.FinishedEvent event) {
                super.uploadFinished(event);
                nextButton.setEnabled(true);
            }
       };
       uploadComponents.discard();
       
       uploadComponents.setButtonCaption(messageSource.getMessage(Message.UPLOAD));
       uploadComponents.setNoFileSelectedText(messageSource.getMessage("NO_FILE_SELECTED"));
       uploadComponents.setSelectedFileText(messageSource.getMessage("SELECTED_IMPORT_FILE"));
       uploadComponents.setDeleteCaption(messageSource.getMessage("CLEAR"));
       uploadComponents.setFieldType(UploadField.FieldType.FILE);
       uploadComponents.setButtonCaption("Browse");
       
       uploadComponents.getRootLayout().setWidth("100%");
       uploadComponents.getRootLayout().setStyleName("bms-upload-container");
       addListenersForUploadField();
    }
    
    public UploadField getUploadComponent(){
    	return uploadComponents;
    }
    
	@Override
	public void instantiateComponents() {
		selectFileLabel = new Label(messageSource.getMessage(Message.SELECT_GERMPLASM_LIST_FILE));
      
		initializeUploadField();
        
        germplasmListUploader = new GermplasmListUploader();
        
        cancelButton = new Button(messageSource.getMessage(Message.CANCEL));
        
        nextButton = new Button();
        nextButton.setData(NEXT_BUTTON_ID);
        nextButton.setEnabled(false);
        nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
        openTemplateButton = new Button();
        openTemplateButton.setImmediate(true);
        openTemplateButton.setStyleName(Reindeer.BUTTON_LINK);
        
	}

	@Override
	public void initializeValues() {
		//do nothing
	}

	public void addListenersForUploadField(){
		uploadComponents.setDeleteButtonListener(new Button.ClickListener() {
			private static final long serialVersionUID = -1357425494204377238L;

			@Override
            public void buttonClick(ClickEvent event) {
               nextButton.setEnabled(false);
            }
        });
		uploadComponents.setFileFactory(germplasmListUploader);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void addListeners() {
		
		addListenersForUploadField();
		
		
		cancelButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				cancelButtonAction();
			}

		});
		
		nextButton.addListener(new GermplasmImportButtonClickListener(this));
		
		openTemplateButton.addListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				ExportGermplasmListTemplateDialog exportGermplasmTemplateDialog = new ExportGermplasmListTemplateDialog(source);
				source.getWindow().addWindow(exportGermplasmTemplateDialog);
			}
		});
	}

	@Override
	public void layoutComponents() {
		addComponent(selectFileLabel, "top:20px");
		addComponent(openTemplateButton, "top:21px;left:520px;");

		addComponent(uploadComponents, "top:50px");

		HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setHeight("40px");
        buttonLayout.setSpacing(true);
        
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(nextButton);
        buttonLayout.setComponentAlignment(cancelButton, Alignment.BOTTOM_RIGHT);
        buttonLayout.setComponentAlignment(nextButton, Alignment.BOTTOM_LEFT);
        
        addComponent(buttonLayout, "top:230px");
	}
	
	public GermplasmListUploader getGermplasmListUploader(){
		return this.germplasmListUploader;
	}
	
	protected void cancelButtonAction() {
		Window window = source.getWindow();
		if (source.getGermplasmImportPopupSource() == null){
			source.reset();
			//if called by Fieldbook
			if (source.isViaPopup() && window != null){
				window.executeJavaScript(FB_CLOSE_WINDOW_JS_CALL);
			}
		} else {
			source.getGermplasmImportPopupSource().getParentWindow().removeWindow((Window) source.getComponentContainer());
		}
	}
}
