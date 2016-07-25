
package org.generationcp.breeding.manager.customcomponent.listinventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.breeding.manager.data.initializer.ListInventoryDataInitializer;
import org.generationcp.breeding.manager.listmanager.ListManagerMain;
import org.generationcp.breeding.manager.listmanager.util.InventoryTableDropHandler;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;

public class ListManagerInventoryTableTest {

	private static final int LIST_ID = 1;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	protected InventoryDataManager inventoryDataManager;

	@Mock
	protected GermplasmListManager germplasmListManager;

	@Mock
	private ListManagerMain listManagerMain;

	@InjectMocks
	private ListManagerInventoryTable listInventoryTable = new ListManagerInventoryTable(this.listManagerMain,
			ListManagerInventoryTableTest.LIST_ID, true, true);


	@Before
	public void setUp() throws MiddlewareQueryException {

		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testInstantiateComponents() {

		listInventoryTable.instantiateComponents();

		// Ensure that the ListManagerInventoryTable is properly initialized.
		Assert.assertEquals(ListManagerInventoryTable.INVENTORY_TABLE_DATA, listInventoryTable.getTable().getData());
		Assert.assertEquals(Table.TableDragMode.ROW, this.listInventoryTable.getTable().getDragMode());
		Assert.assertNotNull(this.listInventoryTable.getTable().getDropHandler());
		Assert.assertEquals(InventoryTableDropHandler.class, this.listInventoryTable.getTable().getDropHandler().getClass());

	}




}
