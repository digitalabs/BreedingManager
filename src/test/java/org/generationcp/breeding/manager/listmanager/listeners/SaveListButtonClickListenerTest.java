package org.generationcp.breeding.manager.listmanager.listeners;

import com.beust.jcommander.internal.Lists;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.apache.commons.lang.reflect.FieldUtils;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.AddColumnContextMenu;
import org.generationcp.breeding.manager.listmanager.ListBuilderComponent;
import org.generationcp.breeding.manager.listmanager.ListManagerMain;
import org.generationcp.breeding.manager.listmanager.ListSelectionComponent;
import org.generationcp.breeding.manager.listmanager.util.BuildNewListDropHandler;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.ListInventoryDataInitializer;
import org.generationcp.middleware.domain.gms.ListDataColumn;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaveListButtonClickListenerTest {

	private SaveListButtonClickListener saveListener;

	@Mock
	private ListBuilderComponent source;

	@Mock
	private GermplasmListManager dataManager;

	@Mock
	private Table listDataTable;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private AddColumnContextMenu addColumnContextMenu;

	private Project project;

	private GermplasmList germplasmList;
	private static final Integer DUMMY_ID = 1;
	private static final Long DUMMY_LONG_ID = 1L;
	private static final String DUMMY_OPTION = "Dummy Option";
	private static final String DUMMY_PROGRAM_UUID = "12345678";

	@Before
	public void setUp() throws IllegalAccessException {
		MockitoAnnotations.initMocks(this);

		this.initializeGermplasmList();
		this.initializeProject();

		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(SaveListButtonClickListenerTest.DUMMY_ID);

		final SaveListButtonClickListener _listener = new SaveListButtonClickListener(this.source, this.listDataTable, this.messageSource);

		FieldUtils.writeDeclaredField(_listener, "contextUtil", this.contextUtil, true);

		this.saveListener = Mockito.spy(_listener);

		Mockito.when(this.source.getWindow()).thenReturn(new Window());

		Mockito.when(this.messageSource.getMessage(Message.NAME_CAN_NOT_BE_BLANK)).thenReturn(SaveListButtonClickListenerTest.DUMMY_OPTION);
		Mockito.when(this.source.getAddColumnContextMenu()).thenReturn(this.addColumnContextMenu);

		this.saveListener.setDataManager(this.dataManager);
		this.saveListener.setMessageSource(this.messageSource);
		this.saveListener.setInventoryDataManager(this.inventoryDataManager);
		this.saveListener.setSource(this.source);
		this.saveListener.setTransactionManager(this.transactionManager);

	}

	private void initializeProject() {
		this.project = new Project();
		this.project.setProjectId(SaveListButtonClickListenerTest.DUMMY_LONG_ID);
	}

	private void initializeGermplasmList() {
		this.germplasmList = new GermplasmList();
		this.germplasmList.setName("List Name");
		this.germplasmList.setDescription("This is a description.");
		this.germplasmList.setDate(20150801L);
		this.germplasmList.setStatus(Integer.valueOf(1));
		this.germplasmList.setUserId(SaveListButtonClickListenerTest.DUMMY_ID);
		this.germplasmList.setProgramUUID(DUMMY_PROGRAM_UUID);
	}

	@Test
	public void testValidateListDetailsForListNameThatIsNull() {
		this.initializeGermplasmList();
		this.germplasmList.setName(null);
		Assert.assertFalse("Expected to invalidate for germplasm list without listname.",
				this.saveListener.validateListDetails(this.germplasmList, null));
	}

	@Test
	public void testValidateListDetailsForListNameThatIsGreaterThan50() {
		this.initializeGermplasmList();
		this.germplasmList.setName("abcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijk");
		Assert.assertFalse("Expected to invalidate for germplasm list with listname.length > 50.",
				this.saveListener.validateListDetails(this.germplasmList, null));
	}

	@Test
	public void testValidateListDetailsForDescriptionThatIsGreaterThan255() {
		this.initializeGermplasmList();
		this.germplasmList.setDescription("abcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijk"
				+ "abcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijk"
				+ "abcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdef"
				+ "ghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijkabcdefghijk");
		Assert.assertFalse("Expected to invalidate for germplasm list with description.length > 255.",
				this.saveListener.validateListDetails(this.germplasmList, null));
	}

	@Test
	public void testValidateListDetailsForDateThatIsNull() {
		this.initializeGermplasmList();
		this.germplasmList.setDate(null);
		Assert.assertFalse("Expected to invalidate for germplasm list without date.",
				this.saveListener.validateListDetails(this.germplasmList, null));

	}

	@Test
	public void testDoSaveActionWhenUnsavedReservationThrowErrorMessageOfUnsavedReservation() {
		final ListManagerMain listManagerMain = Mockito.mock(ListManagerMain.class);
		final ListSelectionComponent listSelectionComponent = Mockito.mock(ListSelectionComponent.class);
		final BuildNewListDropHandler buildNewListDropHandler = Mockito.mock(BuildNewListDropHandler.class);
		Mockito.when(listManagerMain.getListSelectionComponent()).thenReturn(listSelectionComponent);

		Mockito.when(this.source.getSource()).thenReturn(listManagerMain);
		final GermplasmList currentlySavedGermplasmList = GermplasmListTestDataInitializer.createGermplasmListWithListData(1, 1);
		Mockito.when(this.source.getCurrentlySavedGermplasmList()).thenReturn(currentlySavedGermplasmList);
		Mockito.when(this.source.getCurrentlySetGermplasmListInfo()).thenReturn(this.germplasmList);
		Mockito.when(this.source.getBuildNewListDropHandler()).thenReturn(buildNewListDropHandler);
		Mockito.when(this.source.saveListAction()).thenReturn(false);
		Mockito.when(this.dataManager.addGermplasmList(this.germplasmList)).thenReturn(1);
		Mockito.when(this.dataManager.getGermplasmListById(Mockito.isA(Integer.class))).thenReturn(currentlySavedGermplasmList);

		this.saveListener.doSaveAction(true, true);
		Mockito.verify(this.messageSource).getMessage(Message.UNSAVED_RESERVATION_WARNING_WHILE_SAVING_LIST);
	}

	@Test
	public void testDoSaveActionWithListSavedSuccessfully() {
		final ListManagerMain listManagerMain = Mockito.mock(ListManagerMain.class);
		final ListSelectionComponent listSelectionComponent = Mockito.mock(ListSelectionComponent.class);
		final BuildNewListDropHandler buildNewListDropHandler = Mockito.mock(BuildNewListDropHandler.class);
		Mockito.when(listManagerMain.getListSelectionComponent()).thenReturn(listSelectionComponent);

		Mockito.when(this.source.getSource()).thenReturn(listManagerMain);
		final GermplasmList currentlySavedGermplasmList = GermplasmListTestDataInitializer.createGermplasmListWithListData(1, 1);
		currentlySavedGermplasmList.setProgramUUID(null);
		currentlySavedGermplasmList.setStatus(101);
		Mockito.when(this.source.getCurrentlySavedGermplasmList()).thenReturn(currentlySavedGermplasmList);
		Mockito.when(this.source.getCurrentlySetGermplasmListInfo()).thenReturn(this.germplasmList);
		Mockito.when(this.source.getBuildNewListDropHandler()).thenReturn(buildNewListDropHandler);
		Mockito.when(this.source.saveListAction()).thenReturn(true);

		final GermplasmListData germplasmListData = ListInventoryDataInitializer.createGermplasmListData(1);
		Mockito.when(this.source.getListEntriesFromTable()).thenReturn(Lists.newArrayList(germplasmListData));

		Mockito.when(this.dataManager.addGermplasmList(this.germplasmList)).thenReturn(1);
		Mockito.when(this.dataManager.getGermplasmListById(Mockito.isA(Integer.class))).thenReturn(currentlySavedGermplasmList);

		final List<Integer> listDataIds = Lists.newArrayList();
		listDataIds.add(germplasmListData.getId());

		Mockito.when(this.dataManager.addGermplasmListData(Mockito.isA(List.class))).thenReturn(listDataIds);

		Mockito.when(this.inventoryDataManager.getLotCountsForList(Mockito.isA(Integer.class), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(Lists.newArrayList(germplasmListData));

		final Property property = Mockito.mock(Property.class);
		final Item item = Mockito.mock(Item.class);
		Mockito.when(item.getItemProperty(Mockito.any())).thenReturn(property);

		Mockito.when(this.listDataTable.addItem(Mockito.any())).thenReturn(item);

		Mockito.when(this.dataManager.saveListDataColumns(Mockito.isA(List.class))).thenReturn(Lists.<ListDataInfo>newArrayList());

		this.saveListener.doSaveAction(true, true);
		Mockito.verify(this.messageSource).getMessage(Message.LIST_DATA_SAVED_SUCCESS);
		Mockito.verify(this.listDataTable).requestRepaint();

		// Verify that the programUUID and status remain the same and not overwritten by this method.
		Assert.assertNull(currentlySavedGermplasmList.getProgramUUID());
		Assert.assertEquals(101, currentlySavedGermplasmList.getStatus().intValue());

	}

	@Test
	public void testSaveListDataColumns() {
		final List<String> addedColumns = Arrays.asList("NOTES");
		Mockito.doReturn(addedColumns).when(this.source).getAttributeAndNameTypeColumns();
		final List<ListDataInfo> listDataInfo = Arrays.asList(new ListDataInfo(1, Arrays.asList(new ListDataColumn(ColumnLabels.PREFERRED_NAME.getName(), "IBP-001"))));
		Mockito.doReturn(listDataInfo).when(this.addColumnContextMenu).getListDataCollectionFromTable(this.listDataTable, addedColumns);

		this.saveListener.saveListDataColumns(new GermplasmList());
		Mockito.verify(this.dataManager).saveListDataColumns(listDataInfo);
	}

	@Test
	public void testCloneAddedColumnsToTemp() {
		final Table sourceTable = Mockito.mock(Table.class);

		final String noteAttributeField = "NOTE_ATTRIBUTE";
		final String notesPrefix = "NOTES ";
		final String preferredNamePrefix = "PREFERRED NAME ";
		final List<String> addedColumns = new ArrayList<>(Arrays.asList(ColumnLabels.PREFERRED_NAME.getName(), noteAttributeField));
		final List<String> attributeAndNameTypes = Arrays.asList(noteAttributeField);
		Mockito.doReturn(attributeAndNameTypes).when(this.source).getAttributeAndNameTypeColumns();
		Mockito.doReturn(addedColumns).when(this.addColumnContextMenu).getAddedColumns(sourceTable, attributeAndNameTypes);

		final List<Integer> tableEntryIds = Arrays.asList(1, 2, 3);
		Mockito.doReturn(tableEntryIds).when(sourceTable).getItemIds();

		for (final Integer id: tableEntryIds) {
			final Item item = Mockito.mock(Item.class);
			Mockito.doReturn(item).when(sourceTable).getItem(id);
			final Property property1 = Mockito.mock(Property.class);
			Mockito.doReturn(property1).when(item).getItemProperty(ColumnLabels.PREFERRED_NAME.getName());
			Mockito.doReturn(new String(preferredNamePrefix + id)).when(property1).getValue();
			final Property property2 = Mockito.mock(Property.class);
			Mockito.doReturn(property2).when(item).getItemProperty(noteAttributeField);
			Mockito.doReturn(new String(notesPrefix + id)).when(property2).getValue();
		}
		final Table tempTable = this.saveListener.cloneAddedColumnsToTemp(sourceTable);
		Assert.assertEquals(tableEntryIds.size(), tempTable.getItemIds().size());
		for (final Integer id: tableEntryIds) {
			Assert.assertTrue(tempTable.getItemIds().contains(id));
			Assert.assertEquals(new String(preferredNamePrefix + id),
					tempTable.getItem(id).getItemProperty(ColumnLabels.PREFERRED_NAME.getName()).getValue());
			Assert.assertEquals(new String(notesPrefix + id),
					tempTable.getItem(id).getItemProperty(noteAttributeField).getValue());
		}

	}
}
