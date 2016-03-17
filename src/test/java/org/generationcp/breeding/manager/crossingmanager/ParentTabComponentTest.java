
package org.generationcp.breeding.manager.crossingmanager;

import java.util.List;

import org.generationcp.breeding.manager.action.SaveGermplasmListAction;
import org.generationcp.breeding.manager.action.SaveGermplasmListActionFactory;
import org.generationcp.breeding.manager.action.SaveGermplasmListActionSource;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.crossingmanager.settings.ManageCrossingSettingsMain;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customcomponent.listinventory.CrossingManagerInventoryTable;
import org.generationcp.breeding.manager.data.initializer.GermplasmListEntryTestDataInitializer;
import org.generationcp.breeding.manager.data.initializer.ImportedGermplasmListDataInitializer;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.ReserveInventoryActionFactory;
import org.generationcp.breeding.manager.inventory.ReserveInventorySource;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.service.api.PedigreeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@RunWith(MockitoJUnitRunner.class)
public class ParentTabComponentTest {

	private static final int GERMPLASM_LIST_ID = 1;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ManageCrossingSettingsMain makeCrossesSettingsMain;

	@Mock
	private CrossingManagerMakeCrossesComponent crossingManagerMakeCrossesComponent;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private SaveGermplasmListActionFactory saveGermplasmListActionFactory;

	@Mock
	private ReserveInventoryActionFactory reserveInventoryActionFactory;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private Component parent;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private PedigreeService pedigreeService;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private UserDataManager userDataManager;

	@Mock
	private CrossingManagerListTreeComponent crossingManagerListTreeComponent;

	@Mock
	private ReserveInventorySource reserveInventorySource;

	@Spy
	private final Window window = new Window();

	@Captor
	ArgumentCaptor<Window.Notification> captor;

	private ParentTabComponent parentTabComponent;
	private CrossingManagerMakeCrossesComponent makeCrossesMain;
	GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	@Before
	public void setUp() {
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		ManagerFactory.getCurrentManagerFactoryThreadLocal().set(Mockito.mock(ManagerFactory.class));

		final Term fromOntology = new Term();
		fromOntology.setName("Ontology Name");
		Mockito.doReturn(fromOntology).when(this.ontologyDataManager).getTermById(Matchers.anyInt());

		this.makeCrossesMain = new CrossingManagerMakeCrossesComponent(this.makeCrossesSettingsMain);

		final SelectParentsComponent selectParentsComponent = new SelectParentsComponent(this.makeCrossesMain);
		selectParentsComponent.setMessageSource(this.messageSource);
		selectParentsComponent.instantiateComponents();
		selectParentsComponent.setListTreeComponent(this.crossingManagerListTreeComponent);

		this.makeCrossesMain.instantiateComponents();
		this.makeCrossesMain.setModeViewOnly(ModeView.LIST_VIEW);
		final MakeCrossesParentsComponent source = Mockito.spy(new MakeCrossesParentsComponent(this.makeCrossesMain));
		final String parentLabel = "Female Parents";
		final Integer rowCount = 10;
		this.parentTabComponent = new ParentTabComponent(this.makeCrossesMain, source, parentLabel, rowCount,
				this.saveGermplasmListActionFactory, this.reserveInventoryActionFactory);
		source.setMaleParentTab(this.parentTabComponent);
		source.setFemaleParentTab(this.parentTabComponent);
		Mockito.doReturn(this.window).when(source).getWindow();

		this.makeCrossesMain.setParentsComponent(source);
		final MakeCrossesTableComponent crossesTableComponent = new MakeCrossesTableComponent(this.makeCrossesMain);
		crossesTableComponent.setMessageSource(this.messageSource);
		crossesTableComponent.setOntologyDataManager(this.ontologyDataManager);
		crossesTableComponent.instantiateComponents();
		this.makeCrossesMain.setCrossesTableComponent(crossesTableComponent);

		this.makeCrossesMain.setSelectParentsComponent(selectParentsComponent);
		Mockito.doReturn("TestString").when(this.messageSource).getMessage(Matchers.any(Message.class));
		this.parentTabComponent.setMessageSource(this.messageSource);
		this.parentTabComponent.setOntologyDataManager(this.ontologyDataManager);
		this.parentTabComponent.setInventoryDataManager(this.inventoryDataManager);
		Mockito.doReturn(this.window).when(this.parent).getWindow();
		this.parentTabComponent.setParent(this.parent);

		final SaveGermplasmListAction saveGermplasmListAction = new SaveGermplasmListAction(this.parentTabComponent,
				this.germplasmListTestDataInitializer.createGermplasmList(ParentTabComponentTest.GERMPLASM_LIST_ID),
				GermplasmListEntryTestDataInitializer.getGermplasmListEntries());
		saveGermplasmListAction.setContextUtil(this.contextUtil);
		saveGermplasmListAction.setGermplasmListManager(this.germplasmListManager);
		saveGermplasmListAction.setPedigreeService(this.pedigreeService);
		saveGermplasmListAction.setInventoryDataManager(this.inventoryDataManager);
		Mockito.doReturn(this.germplasmListTestDataInitializer.createGermplasmList(ParentTabComponentTest.GERMPLASM_LIST_ID))
				.when(this.germplasmListManager).getGermplasmListById(Matchers.anyInt());
		Mockito.doReturn(saveGermplasmListAction).when(this.saveGermplasmListActionFactory).createInstance(
				Matchers.any(SaveGermplasmListActionSource.class), Matchers.any(GermplasmList.class), Matchers.any(List.class));

		final ReserveInventoryAction reserveInventoryAction = new ReserveInventoryAction(this.reserveInventorySource);
		reserveInventoryAction.setContextUtil(this.contextUtil);
		reserveInventoryAction.setInventoryDataManager(this.inventoryDataManager);
		reserveInventoryAction.setUserDataManager(this.userDataManager);
		Mockito.doReturn(reserveInventoryAction).when(this.reserveInventoryActionFactory)
				.createInstance(Matchers.any(ReserveInventorySource.class));
		final Person person = new Person();
		person.setId(123);
		Mockito.doReturn(person).when(this.userDataManager).getPersonById(Matchers.anyInt());
	}

	@Test
	public void testInitializeParentTable_returnsTheValueFromColumLabelDefaultName() {
		final Integer rowCount = 10;
		this.parentTabComponent.setRowCount(rowCount);

		Mockito.when(this.messageSource.getMessage(Message.CHECK_ICON)).thenReturn("TAG");
		Mockito.when(this.messageSource.getMessage(Message.HASHTAG)).thenReturn("HASHTAG");

		final Term fromOntologyDesignation = new Term();
		fromOntologyDesignation.setName("DESIGNATION");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.DESIG.getId())).thenReturn(fromOntologyDesignation);
		final Term fromOntologyAvailableInventory = new Term();
		fromOntologyAvailableInventory.setName("LOTS AVAILABLE");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.AVAILABLE_INVENTORY.getId())).thenReturn(fromOntologyAvailableInventory);
		final Term fromOntologySeedReservation = new Term();
		fromOntologySeedReservation.setName("SEED RES");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.SEED_RESERVATION.getId())).thenReturn(fromOntologySeedReservation);
		final Term fromOntologyStockId = new Term();
		fromOntologyStockId.setName("STOCKID");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.STOCKID.getId())).thenReturn(fromOntologyStockId);

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.parentTabComponent.initializeParentTable(tableWithSelectAll);

		final Table table = tableWithSelectAll.getTable();

		Assert.assertEquals("TAG", table.getColumnHeader(ColumnLabels.TAG.getName()));
		Assert.assertEquals("HASHTAG", table.getColumnHeader(ColumnLabels.ENTRY_ID.getName()));
		Assert.assertEquals("DESIGNATION", table.getColumnHeader(ColumnLabels.DESIGNATION.getName()));
		Assert.assertEquals("LOTS AVAILABLE", table.getColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertEquals("SEED RES", table.getColumnHeader(ColumnLabels.SEED_RESERVATION.getName()));
		Assert.assertEquals("STOCKID", table.getColumnHeader(ColumnLabels.STOCKID.getName()));
	}

	@Test
	public void testInitializeParentTable_returnsTheValueFromOntologyManager() throws MiddlewareQueryException {
		final Integer rowCount = 10;
		this.parentTabComponent.setRowCount(rowCount);

		final Term fromOntologyDesignation = new Term();
		fromOntologyDesignation.setName("DESIGNATION");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.DESIG.getId())).thenReturn(fromOntologyDesignation);
		final Term fromOntologyAvailableInventory = new Term();
		fromOntologyAvailableInventory.setName("LOTS AVAILABLE");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.AVAILABLE_INVENTORY.getId())).thenReturn(fromOntologyAvailableInventory);
		final Term fromOntologySeedReservation = new Term();
		fromOntologySeedReservation.setName("SEED RES");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.SEED_RESERVATION.getId())).thenReturn(fromOntologySeedReservation);
		final Term fromOntologyStockId = new Term();
		fromOntologyStockId.setName("STOCKID");
		Mockito.when(this.ontologyDataManager.getTermById(TermId.STOCKID.getId())).thenReturn(fromOntologyStockId);

		Mockito.when(this.messageSource.getMessage(Message.CHECK_ICON)).thenReturn("TAG");
		Mockito.when(this.messageSource.getMessage(Message.HASHTAG)).thenReturn("HASHTAG");

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.parentTabComponent.initializeParentTable(tableWithSelectAll);

		final Table table = tableWithSelectAll.getTable();

		Assert.assertEquals("TAG", table.getColumnHeader(ColumnLabels.TAG.getName()));
		Assert.assertEquals("HASHTAG", table.getColumnHeader(ColumnLabels.ENTRY_ID.getName()));
		Assert.assertEquals("DESIGNATION", table.getColumnHeader(ColumnLabels.DESIGNATION.getName()));
		Assert.assertEquals("LOTS AVAILABLE", table.getColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertEquals("SEED RES", table.getColumnHeader(ColumnLabels.SEED_RESERVATION.getName()));
		Assert.assertEquals("STOCKID", table.getColumnHeader(ColumnLabels.STOCKID.getName()));
	}

	@Test
	public void testDoSaveActionOpenWindowForSaving() {
		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.parentTabComponent.initializeMainComponents();
		this.parentTabComponent.initializeParentTable(tableWithSelectAll);
		final CrossingManagerInventoryTable inventoryTable = new CrossingManagerInventoryTable(null);
		inventoryTable.setMessageSource(this.messageSource);
		inventoryTable.setOntologyDataManager(this.ontologyDataManager);
		inventoryTable.instantiateComponents();
		this.parentTabComponent.initializeListInventoryTable(inventoryTable);
		this.parentTabComponent.addListeners();
		this.parentTabComponent.setHasChanges(true);

		// function to test
		this.parentTabComponent.doSaveAction();
		Assert.assertNotNull(this.parentTabComponent.getSaveListAsWindow());
	}

	@Test
	public void testDoSaveActionWithNoChanges() {
		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.parentTabComponent.initializeMainComponents();
		this.parentTabComponent.initializeParentTable(tableWithSelectAll);
		final CrossingManagerInventoryTable inventoryTable = new CrossingManagerInventoryTable(null);
		inventoryTable.setMessageSource(this.messageSource);
		inventoryTable.setOntologyDataManager(this.ontologyDataManager);
		inventoryTable.instantiateComponents();
		this.parentTabComponent.initializeListInventoryTable(inventoryTable);
		this.parentTabComponent.addListeners();
		this.parentTabComponent.setHasChanges(false);

		// function to test
		this.parentTabComponent.doSaveAction();
		Assert.assertNull(this.parentTabComponent.getSaveListAsWindow());
	}

	@Test
	public void testDoSaveActionSavingSilently() {
		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.parentTabComponent.initializeMainComponents();
		this.parentTabComponent.initializeParentTable(tableWithSelectAll);
		final CrossingManagerInventoryTable inventoryTable = new CrossingManagerInventoryTable(null);
		inventoryTable.setMessageSource(this.messageSource);
		inventoryTable.setOntologyDataManager(this.ontologyDataManager);
		inventoryTable.instantiateComponents();
		this.parentTabComponent.initializeListInventoryTable(inventoryTable);
		this.parentTabComponent.addListeners();
		this.parentTabComponent.setHasChanges(true);
		this.parentTabComponent
				.setGermplasmList(this.germplasmListTestDataInitializer.createGermplasmList(ParentTabComponentTest.GERMPLASM_LIST_ID));

		// function to test
		this.parentTabComponent.doSaveAction();
		Assert.assertFalse(this.parentTabComponent.hasUnsavedChanges());
		Assert.assertFalse(this.parentTabComponent.isTreatAsNewList());
	}

	@Test
	public void testDoSaveActionSaveReservations() {
		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.makeCrossesMain.setModeViewOnly(ModeView.INVENTORY_VIEW);

		this.parentTabComponent.initializeMainComponents();
		this.parentTabComponent.initializeParentTable(tableWithSelectAll);
		final CrossingManagerInventoryTable inventoryTable = new CrossingManagerInventoryTable(null);
		inventoryTable.setMessageSource(this.messageSource);
		inventoryTable.setOntologyDataManager(this.ontologyDataManager);
		inventoryTable.instantiateComponents();
		this.parentTabComponent.initializeListInventoryTable(inventoryTable);
		this.parentTabComponent.addListeners();
		this.parentTabComponent.setHasChanges(true);
		this.parentTabComponent
				.setGermplasmList(this.germplasmListTestDataInitializer.createGermplasmList(ParentTabComponentTest.GERMPLASM_LIST_ID));
		this.parentTabComponent.setValidReservationsToSave(ImportedGermplasmListDataInitializer.createReservations(2));

		// function to test
		this.parentTabComponent.doSaveAction();
		Mockito.verify(this.window).showNotification(this.captor.capture());
		this.captor.getValue().getCaption();
		Assert.assertEquals("</br>All reservations were saved.", this.captor.getValue().getDescription());

	}

}
