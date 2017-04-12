
package org.generationcp.breeding.manager.crossingmanager;

import java.util.List;

import com.vaadin.ui.Alignment;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.crossingmanager.constants.CrossType;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerImportButtonClickListener;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.Runo;

@Configurable
public class CrossingMethodComponent extends VerticalLayout implements BreedingManagerLayout, InitializingBean,
		InternationalizableComponent {

	private static final long serialVersionUID = -8847158352169444182L;

	public static final String GENERATE_CROSS_BUTTON_ID = "Generate Cross Button";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Panel crossingMethodPanel;
	private Label crossingMethodLabel;
	private Label crossingMethodComboBoxLabel;

	private ComboBox crossingMethodComboBox;
	private CheckBox chkBoxMakeReciprocalCrosses;
	private CheckBox chkBoxExcludeSelfs;

	private Button btnGenerateCross;

	private final CrossingManagerMakeCrossesComponent makeCrossesMain;
	private MakeCrossesParentsComponent parentsComponent;

	public CrossingMethodComponent(CrossingManagerMakeCrossesComponent makeCrossesMain) {
		super();
		this.makeCrossesMain = makeCrossesMain;
	}

	@Override
	public void updateLabels() {
		// not implemented
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {
		this.crossingMethodLabel = new Label(this.messageSource.getMessage(Message.CROSSING_METHOD));
		this.crossingMethodLabel.setDebugId("crossingMethodLabel");
		this.crossingMethodLabel.setWidth("200px");
		this.crossingMethodLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		this.crossingMethodLabel.addStyleName(AppConstants.CssStyles.BOLD);

		this.crossingMethodComboBoxLabel = new Label(this.messageSource.getMessage(Message.CROSSING_METHOD_LABEL));
		this.crossingMethodComboBoxLabel.setDebugId("crossingMethodComboBoxLabel");

		this.crossingMethodComboBox = new ComboBox();
		this.crossingMethodComboBox.setDebugId("crossingMethodComboBox");
		this.crossingMethodComboBox.setNewItemsAllowed(false);
		this.crossingMethodComboBox.setNullSelectionAllowed(false);
		this.crossingMethodComboBox.setWidth("280px");

		this.chkBoxMakeReciprocalCrosses = new CheckBox(this.messageSource.getMessage(Message.MAKE_CROSSES_CHECKBOX_LABEL));
		this.chkBoxMakeReciprocalCrosses.setDebugId("chkBoxMakeReciprocalCrosses");
		//By default set "Exclude self" checkbox as selected
		this.chkBoxExcludeSelfs = new CheckBox(this.messageSource.getMessage(Message.EXCLUDE_SELFS_LABEL), true);
		this.chkBoxExcludeSelfs.setDebugId("chkBoxExcludeSelfs");

		this.btnGenerateCross = new Button(this.messageSource.getMessage(Message.GENERATE_CROSSES_BUTTON_LABEL));
		this.btnGenerateCross.setDebugId("btnGenerateCross");
		this.btnGenerateCross.setData(CrossingMethodComponent.GENERATE_CROSS_BUTTON_ID);
		this.btnGenerateCross.addStyleName(Bootstrap.Buttons.INFO.styleName());
	}

	@Override
	public void initializeValues() {
		this.crossingMethodComboBox.addItem(CrossType.PLEASE_CHOOSE);
		this.crossingMethodComboBox.setItemCaption(CrossType.PLEASE_CHOOSE,
				this.messageSource.getMessage(Message.MAKE_CROSSES_OPTION_GROUP_ITEM_PLEASE_CHOOSE));
		this.crossingMethodComboBox.addItem(CrossType.MULTIPLY);
		this.crossingMethodComboBox.setItemCaption(CrossType.MULTIPLY,
				this.messageSource.getMessage(Message.MAKE_CROSSES_OPTION_GROUP_ITEM_ONE_LABEL));
		this.crossingMethodComboBox.addItem(CrossType.TOP_TO_BOTTOM);
		this.crossingMethodComboBox.setItemCaption(CrossType.TOP_TO_BOTTOM,
				this.messageSource.getMessage(Message.MAKE_CROSSES_OPTION_GROUP_ITEM_TWO_LABEL));
		this.crossingMethodComboBox.select(CrossType.PLEASE_CHOOSE);
	}

	@Override
	public void addListeners() {
		this.btnGenerateCross.addListener(new CrossingManagerImportButtonClickListener(this));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		this.setWidth("950px");

		HorizontalLayout layoutCrossingMethodOption = new HorizontalLayout();
		layoutCrossingMethodOption.setDebugId("layoutCrossingMethodOption");
		layoutCrossingMethodOption.setSpacing(true);
		layoutCrossingMethodOption.setHeight("40px");
		layoutCrossingMethodOption.addComponent(this.crossingMethodComboBoxLabel);
		layoutCrossingMethodOption.addComponent(this.crossingMethodComboBox);

		HorizontalLayout layoutchkBoxOption = new HorizontalLayout();
		layoutchkBoxOption.setDebugId("layoutchkBoxOption");
		layoutchkBoxOption.setSpacing(true);
		layoutchkBoxOption.setHeight("40px");
		layoutchkBoxOption.addComponent(this.chkBoxMakeReciprocalCrosses);
		layoutchkBoxOption.addComponent(this.chkBoxExcludeSelfs);
		layoutchkBoxOption.addComponent(this.btnGenerateCross);


		this.crossingMethodPanel = new Panel();
		this.crossingMethodPanel.setDebugId("crossingMethodPanel");
		this.crossingMethodPanel.addStyleName(Runo.PANEL_LIGHT); //"section_panel_layout ");
		this.crossingMethodPanel.setHeight("160px");

		HorizontalLayout crossingMethodLayout = new HorizontalLayout();
		crossingMethodLayout.setDebugId("crossingMethodLayout");
		crossingMethodLayout.setSpacing(true);
		crossingMethodLayout.setHeight("40px");
		crossingMethodLayout.addComponent(this.crossingMethodLabel);

		this.crossingMethodPanel.addComponent(crossingMethodLayout);
		this.crossingMethodPanel.addComponent(layoutCrossingMethodOption);
		this.crossingMethodPanel.addComponent(layoutchkBoxOption);

		this.addComponent(this.crossingMethodPanel);
	}

	public void makeCrossButtonAction() {
		// TODO temporary fix, will have a new fix approach soon
		if (this.makeCrossesMain.getModeView().equals(ModeView.INVENTORY_VIEW)) {
			String message = "Please switch to list view first before making crosses.";
			MessageNotifier.showError(this.getWindow(), "Warning!", message);
		} else {
			CrossType type = (CrossType) this.crossingMethodComboBox.getValue();
			if (CrossType.PLEASE_CHOOSE.equals(type)) {
				MessageNotifier.showWarning(this.getWindow(), this.messageSource.getMessage(Message.WARNING),
						this.messageSource.getMessage(Message.PLEASE_CHOOSE_CROSSING_METHOD));
			} else {
				this.parentsComponent = this.makeCrossesMain.getParentsComponent();

				Table femaleParents = this.parentsComponent.getFemaleTable();
				Table maleParents = this.parentsComponent.getMaleTable();

				List<GermplasmListEntry> femaleList = this.parentsComponent.getCorrectSortedValue(femaleParents);
				List<GermplasmListEntry> maleList = this.parentsComponent.getCorrectSortedValue(maleParents);
				this.parentsComponent.updateFemaleListNameForCrosses();
				this.parentsComponent.updateMaleListNameForCrosses();

				this.makeCrossesMain.makeCrossButtonAction(femaleList, maleList, this.parentsComponent.getFemaleListNameForCrosses(),
						this.parentsComponent.getMaleListNameForCrosses(), type, this.chkBoxMakeReciprocalCrosses.booleanValue(),
						this.chkBoxExcludeSelfs.booleanValue());
			}
		}
	}
}
