
package org.generationcp.breeding.manager.listmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.breeding.manager.application.BreedingManagerApplication;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.listmanager.util.ListDataPropertiesRenderer;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class ListComponentTest {

	private static final String STOCKID = "STOCKID";
	private static final String SEED_RES = "SEED_RES";
	private static final String AVAIL_INV = "AVAIL_INV";
	private static final String HASH = "#";
	private static final String CHECK = "CHECK";
	private static final String SEED_SOURCE = "SEED_SOURCE";
	private static final String CROSS = "CROSS";
	private static final String DESIG = "DESIG";
	private static final String ENTRY_CODE = "ENTRY_CODE";
	private static final String GID = "GID";

	private static final String UPDATED_GERMPLASM_LIST_NOTE = "UPDATED Germplasm List Note";
	private static final String UPDATED_GERMPLASM_LIST_NAME = "UPDATED Germplasm List Name";
	private static final String UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE = "UPDATED Germplasm List Description Value";
	private static final long UPDATED_GERMPLASM_LIST_DATE = 20141205;
	private static final String UPDATED_GERMPLASM_LIST_TYPE = "F1 LST";

	private static final Integer EXPECTED_USER_ID = 1;
	private static final Integer TEST_GERMPLASM_LIST_ID = 111;
	private static final Integer TEST_GERMPLASM_NO_OF_ENTRIES = 5;

	@Mock
	private ListManagerMain source;

	@Mock
	private ListTabComponent parentListDetailsComponent;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private Window window;

	@Mock
	private AddColumnContextMenu addColumnContextMenu;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ListSelectionComponent listSelectionComponent;

	@Mock
	private ListSelectionLayout listDetailsLayout;

	@Mock
	private BreedingManagerApplication breedingManagerApplication;

	@Mock
	private ListDataPropertiesRenderer newColumnsRenderer;

	@InjectMocks
	private final ListComponent listComponent = new ListComponent();

	private GermplasmList germplasmList;

	private GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	@Before
	public void setUp() throws Exception {

		this.setUpWorkbenchDataManager();
		this.setUpOntologyManager();

		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		Mockito.when(this.messageSource.getMessage(Matchers.any(Message.class))).thenReturn("");
		Mockito.when(this.messageSource.getMessage(Message.CHECK_ICON)).thenReturn(ListComponentTest.CHECK);
		Mockito.when(this.messageSource.getMessage(Message.HASHTAG)).thenReturn(ListComponentTest.HASH);

		this.germplasmList = this.germplasmListTestDataInitializer.createGermplasmListWithListData(ListComponentTest.TEST_GERMPLASM_LIST_ID,
				ListComponentTest.TEST_GERMPLASM_NO_OF_ENTRIES);
		this.germplasmList.setStatus(1);
		this.listComponent.setListEntries(this.germplasmList.getListData());
		this.listComponent.setGermplasmList(this.germplasmList);

		Mockito.when(this.germplasmListManager.countGermplasmListDataByListId(ListComponentTest.TEST_GERMPLASM_LIST_ID))
				.thenReturn(Long.valueOf(ListComponentTest.TEST_GERMPLASM_NO_OF_ENTRIES));

		Mockito.when(this.germplasmListManager.getGermplasmListById(ListComponentTest.TEST_GERMPLASM_LIST_ID))
				.thenReturn(this.germplasmList);
		Mockito.when(this.germplasmListManager.getAdditionalColumnsForList(ListComponentTest.TEST_GERMPLASM_LIST_ID))
				.thenReturn(this.createGermplasmListNewColumnInfo(ListComponentTest.TEST_GERMPLASM_LIST_ID));

		Mockito.doNothing().when(this.contextUtil).logProgramActivity(Matchers.anyString(), Matchers.anyString());

		Mockito.when(this.source.getModeView()).thenReturn(ModeView.LIST_VIEW);
		Mockito.when(this.source.getWindow()).thenReturn(this.window);
		Mockito.when(this.source.getListSelectionComponent()).thenReturn(this.listSelectionComponent);
		Mockito.when(this.listSelectionComponent.getListDetailsLayout()).thenReturn(this.listDetailsLayout);

	}

	@Test
	public void testSaveList_OverwriteExistingGermplasmList() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(ListComponentTest.TEST_GERMPLASM_LIST_ID);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		try {
			Mockito.doReturn(this.germplasmList).when(this.germplasmListManager).getGermplasmListById(this.germplasmList.getId());

			this.listComponent.saveList(germplasmListToBeSaved);

			final GermplasmList savedList = this.listComponent.getGermplasmList();

			Assert.assertEquals(savedList.getId(), germplasmListToBeSaved.getId());
			Assert.assertEquals(savedList.getDescription(), germplasmListToBeSaved.getDescription());
			Assert.assertEquals(savedList.getName(), germplasmListToBeSaved.getName());
			Assert.assertEquals(savedList.getNotes(), germplasmListToBeSaved.getNotes());
			Assert.assertEquals(savedList.getDate(), germplasmListToBeSaved.getDate());
			Assert.assertEquals(savedList.getType(), germplasmListToBeSaved.getType());
			Assert.assertEquals(savedList.getStatus(), germplasmListToBeSaved.getStatus());

			Assert.assertSame(savedList, this.germplasmList);

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSaveList_OverwriteExistingGermplasmListWithDifferentID() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(1000);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		try {
			Mockito.doNothing().when(this.source).closeList(germplasmListToBeSaved);
			Mockito.doReturn(germplasmListToBeSaved).when(this.germplasmListManager).getGermplasmListById(Matchers.anyInt());

			// this will overwrite the list entries of the current germplasm list. Germplasm List Details will not be updated.
			this.listComponent.saveList(germplasmListToBeSaved);

			final GermplasmList savedList = this.listComponent.getGermplasmList();

			Assert.assertFalse(germplasmListToBeSaved.getId().equals(savedList.getId()));
			Assert.assertFalse(germplasmListToBeSaved.getDescription().equals(savedList.getDescription()));
			Assert.assertFalse(germplasmListToBeSaved.getName().equals(savedList.getName()));
			Assert.assertFalse(germplasmListToBeSaved.getNotes().equals(savedList.getNotes()));
			Assert.assertFalse(germplasmListToBeSaved.getDate().equals(savedList.getDate()));
			Assert.assertFalse(germplasmListToBeSaved.getType().equals(savedList.getType()));

			Assert.assertSame(savedList, this.germplasmList);

		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSaveList_OverwriteNonExistingGermplasmList() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(ListComponentTest.TEST_GERMPLASM_LIST_ID);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		Mockito.doReturn(null).when(this.germplasmListManager).getGermplasmListById(ListComponentTest.TEST_GERMPLASM_LIST_ID);

		this.listComponent.saveList(germplasmListToBeSaved);

		final GermplasmList savedList = this.listComponent.getGermplasmList();

		Assert.assertTrue(germplasmListToBeSaved.getId().equals(savedList.getId()));
		Assert.assertFalse(germplasmListToBeSaved.getDescription().equals(savedList.getDescription()));
		Assert.assertFalse(germplasmListToBeSaved.getName().equals(savedList.getName()));
		Assert.assertFalse(germplasmListToBeSaved.getNotes().equals(savedList.getNotes()));
		Assert.assertFalse(germplasmListToBeSaved.getDate().equals(savedList.getDate()));
		Assert.assertFalse(germplasmListToBeSaved.getType().equals(savedList.getType()));

		Assert.assertSame(savedList, this.germplasmList);

	}

	@Test
	public void testInitializeListDataTable() {

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);

		final Table table = tableWithSelectAll.getTable();

		Assert.assertEquals(ListComponentTest.CHECK, table.getColumnHeader(ColumnLabels.TAG.getName()));
		Assert.assertEquals(ListComponentTest.HASH, table.getColumnHeader(ColumnLabels.ENTRY_ID.getName()));
		Assert.assertEquals(ListComponentTest.AVAIL_INV, table.getColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertEquals(ListComponentTest.SEED_RES, table.getColumnHeader(ColumnLabels.SEED_RESERVATION.getName()));
		Assert.assertEquals(ListComponentTest.STOCKID, table.getColumnHeader(ColumnLabels.STOCKID.getName()));
		Assert.assertEquals(ListComponentTest.GID, table.getColumnHeader(ColumnLabels.GID.getName()));
		Assert.assertEquals(ListComponentTest.ENTRY_CODE, table.getColumnHeader(ColumnLabels.ENTRY_CODE.getName()));
		Assert.assertEquals(ListComponentTest.DESIG, table.getColumnHeader(ColumnLabels.DESIGNATION.getName()));
		Assert.assertEquals(ListComponentTest.CROSS, table.getColumnHeader(ColumnLabels.PARENTAGE.getName()));
		Assert.assertEquals(ListComponentTest.SEED_SOURCE, table.getColumnHeader(ColumnLabels.SEED_SOURCE.getName()));

	}

	@Test
	public void testLockGermplasmList() {
		final ContextUtil contextUtil = Mockito.mock(ContextUtil.class);
		this.listComponent.setContextUtil(contextUtil);
		Mockito.doNothing().when(contextUtil).logProgramActivity(Matchers.anyString(), Matchers.anyString());
		Mockito.doReturn("Test").when(this.messageSource).getMessage(Matchers.any(Message.class));
		Mockito.doReturn(this.germplasmList).when(this.germplasmListManager).getGermplasmListById(this.germplasmList.getId());
		this.listComponent.instantiateComponents();
		this.listComponent.getViewListHeaderWindow().instantiateComponents();

		this.listComponent.toggleGermplasmListStatus();

		Assert.assertEquals("Expecting the that the germplasmList status was changed to locked(101) but returned ("
				+ this.germplasmList.getStatus() + ")", Integer.valueOf(101), this.germplasmList.getStatus());
		Assert.assertEquals(Integer.valueOf(101), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(101),
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getGermplasmList().getStatus());
		Assert.assertEquals("Locked List",
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getStatusValueLabel().toString());
	}

	@Test
	public void testUnlockGermplasmList() {

		Mockito.when(this.messageSource.getMessage(Matchers.any(Message.class))).thenReturn("");

		this.germplasmList.setStatus(101);
		this.listComponent.setListDataTable(new Table());
		this.listComponent.instantiateComponents();
		this.listComponent.getViewListHeaderWindow().instantiateComponents();

		this.listComponent.toggleGermplasmListStatus();

		Assert.assertEquals("Expecting the that the germplasmList status was changed to unlocked(1) but returned ("
				+ this.germplasmList.getStatus() + ")", Integer.valueOf(1), this.germplasmList.getStatus());
		Assert.assertEquals(Integer.valueOf(1), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(1), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(1),
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getGermplasmList().getStatus());
		Assert.assertEquals("Unlocked List",
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getStatusValueLabel().toString());
	}

	@Test
	public void testSaveChangesAction_verifyIfTheListTreeIsRefreshedAfterSavingList() {

		Mockito.when(this.messageSource.getMessage(Matchers.any(Message.class))).thenReturn("");

		final Table listDataTable = new Table();
		this.listComponent.setAddColumnContextMenu(this.addColumnContextMenu);
		this.listComponent.instantiateComponents();

		Mockito.when(this.addColumnContextMenu.getListDataCollectionFromTable(listDataTable)).thenReturn(new ArrayList<ListDataInfo>());

		this.listComponent.setListDataTable(listDataTable);
		this.listComponent.saveChangesAction(this.window, false);

	}

	@Test
	public void testIsInventoryColumn() {
		Assert.assertTrue("Expecting AVAILABLE_INVENTORY as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertTrue("Expecting SEED_RESERVATION as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.SEED_RESERVATION.getName()));
		Assert.assertTrue("Expecting STOCKID as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.STOCKID.getName()));
		Assert.assertFalse("Expecting ENTRY_ID as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.ENTRY_ID.getName()));
	}

	@Test
	public void testDeleteRemovedGermplasmEntriesFromTableAllEntries() {

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();
		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);

		this.listComponent.deleteRemovedGermplasmEntriesFromTable();

		Mockito.verify(this.germplasmListManager).deleteGermplasmListDataByListId(ListComponentTest.TEST_GERMPLASM_LIST_ID);

	}

	@Test
	public void testDeleteRemovedGermplasmEntriesFromTableOnlySelectedEntries() {

		final GermplasmList germplasmListWithInventoryInfo =
				this.germplasmListTestDataInitializer.createGermplasmListWithListDataAndInventoryInfo(
						ListComponentTest.TEST_GERMPLASM_LIST_ID, ListComponentTest.TEST_GERMPLASM_NO_OF_ENTRIES);
		Mockito.when(this.inventoryDataManager.getLotCountsForList(ListComponentTest.TEST_GERMPLASM_LIST_ID, 0,
				ListComponentTest.TEST_GERMPLASM_NO_OF_ENTRIES)).thenReturn(germplasmListWithInventoryInfo.getListData());

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();
		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);
		this.listComponent.initializeValues();

		// Add one item to delete from list data table
		this.listComponent.getItemsToDelete().putAll(this.createItemsToDelete(this.listComponent.getListDataTable()));

		this.listComponent.deleteRemovedGermplasmEntriesFromTable();

		// deleteGermplasmListDataByListIdLrecId should only be called once
		Mockito.verify(this.germplasmListManager, Mockito.times(1))
				.deleteGermplasmListDataByListIdLrecId(Matchers.eq(ListComponentTest.TEST_GERMPLASM_LIST_ID), Matchers.anyInt());

		Assert.assertTrue(this.listComponent.getItemsToDelete().isEmpty());

	}

	private void setUpOntologyManager() {

		Mockito.when(this.ontologyDataManager.getTermById(TermId.AVAILABLE_INVENTORY.getId()))
				.thenReturn(this.createTerm(TermId.AVAILABLE_INVENTORY.getId(), ListComponentTest.AVAIL_INV));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.SEED_RESERVATION.getId()))
				.thenReturn(this.createTerm(TermId.SEED_RESERVATION.getId(), ListComponentTest.SEED_RES));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.GID.getId()))
				.thenReturn(this.createTerm(TermId.GID.getId(), ListComponentTest.GID));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.ENTRY_CODE.getId()))
				.thenReturn(this.createTerm(TermId.ENTRY_CODE.getId(), ListComponentTest.ENTRY_CODE));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.DESIG.getId()))
				.thenReturn(this.createTerm(TermId.DESIG.getId(), ListComponentTest.DESIG));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.CROSS.getId()))
				.thenReturn(this.createTerm(TermId.CROSS.getId(), ListComponentTest.CROSS));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.SEED_SOURCE.getId()))
				.thenReturn(this.createTerm(TermId.SEED_SOURCE.getId(), ListComponentTest.SEED_SOURCE));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.STOCKID.getId()))
				.thenReturn(this.createTerm(TermId.STOCKID.getId(), ListComponentTest.STOCKID));

	}

	private void setUpWorkbenchDataManager() {

		final WorkbenchRuntimeData runtimeDate = new WorkbenchRuntimeData();
		runtimeDate.setUserId(5);

		final Project dummyProject = new Project();
		dummyProject.setProjectId((long) 5);

		try {
			Mockito.when(this.workbenchDataManager.getWorkbenchRuntimeData()).thenReturn(runtimeDate);
			Mockito.when(this.workbenchDataManager.getLastOpenedProject(runtimeDate.getUserId())).thenReturn(dummyProject);
			Mockito.when(this.workbenchDataManager.getLocalIbdbUserId(runtimeDate.getUserId(), dummyProject.getProjectId()))
					.thenReturn(ListComponentTest.EXPECTED_USER_ID);

		} catch (final MiddlewareQueryException e) {
			Assert.fail("Failed to create an ibdbuser instance.");
		}
	}

	private Term createTerm(final int id, final String name) {
		final Term term = new Term(id, name, "");
		return term;
	}

	private GermplasmListNewColumnsInfo createGermplasmListNewColumnInfo(final int listId) {
		final GermplasmListNewColumnsInfo germplasmListNewColumnsInfo = new GermplasmListNewColumnsInfo(listId);
		germplasmListNewColumnsInfo.setColumnValuesMap(new HashMap<String, List<ListDataColumnValues>>());
		return germplasmListNewColumnsInfo;
	}

	private Map<Object, String> createItemsToDelete(final Table table) {

		final Map<Object, String> itemsToDelete = new HashMap<>();

		// delete the first record from the germplasm list data table
		itemsToDelete.put(1, "Designation 1");

		return itemsToDelete;
	}

}
