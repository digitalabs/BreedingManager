
package org.generationcp.breeding.manager.customcomponent.listinventory;

import java.util.List;

import org.generationcp.breeding.manager.listmanager.ListManagerMain;
import org.generationcp.breeding.manager.listmanager.listeners.GidLinkButtonClickListener;
import org.generationcp.breeding.manager.listmanager.util.InventoryTableDropHandler;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.themes.BaseTheme;

/**
 * This table is used for displaying lots in Inventory view specifically for List Manager. It basically handles the drop handling method in
 * inventory view.
 */
public class ListManagerInventoryTable extends ListInventoryTable {

	private static final Logger LOG = LoggerFactory.getLogger(ListManagerInventoryTable.class);

	private static final long serialVersionUID = 7827387488704418083L;
	public static final String INVENTORY_TABLE_DATA = "BuildNewListInventoryTableData";

	private final ListManagerMain listManagerMain;
	private InventoryTableDropHandler inventoryTableDropHandler;
	private final Boolean enableDragSource;
	private final Boolean enableDropHandler;

	public ListManagerInventoryTable(ListManagerMain listManagerMain, Integer listId, Boolean enableDragSource, Boolean enableDropHandler) {
		super(listId);
		this.listManagerMain = listManagerMain;
		this.enableDragSource = enableDragSource;
		this.enableDropHandler = enableDropHandler;
	}

	@Override
	public void instantiateComponents() {
		super.instantiateComponents();

		this.listInventoryTable.setData(ListManagerInventoryTable.INVENTORY_TABLE_DATA);
		this.setDragSource();
		this.setDropHandler();
	}

	@Override
	public void displayInventoryDetails(List<GermplasmListData> inventoryDetails) {

		this.listInventoryTable.removeAllItems();
		for (GermplasmListData inventoryDetail : inventoryDetails) {

			Integer entryId = inventoryDetail.getEntryId();
			String designation = inventoryDetail.getDesignation();

			ListDataInventory listDataInventory = inventoryDetail.getInventoryInfo();
			@SuppressWarnings("unchecked")
			List<ListEntryLotDetails> lotDetails = (List<ListEntryLotDetails>) listDataInventory.getLotRows();

			if (lotDetails != null) {
				for (ListEntryLotDetails lotDetail : lotDetails) {
					Item newItem = this.listInventoryTable.addItem(lotDetail);

					CheckBox itemCheckBox = new CheckBox();
					itemCheckBox.setData(lotDetail);
					itemCheckBox.setImmediate(true);
					itemCheckBox.addListener(new ClickListener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
							CheckBox itemCheckBox = (CheckBox) event.getButton();
							ListManagerInventoryTable.this.toggleSelectOnLotEntries(itemCheckBox);
						}

					});

					GermplasmListData germplasmListData = this.retrieveGermplasmListDataUsingLrecId(lotDetail);

					Button desigButton =
							new Button(String.format("%s", designation), new GidLinkButtonClickListener(this.listManagerMain,
									germplasmListData.getGid().toString(), true, true));
					desigButton.setStyleName(BaseTheme.BUTTON_LINK);

					Location locationOfLot = lotDetail.getLocationOfLot();
					String location = "";
					if (locationOfLot != null) {
						location = locationOfLot.getLname();
					}

					Term scaleOfLot = lotDetail.getScaleOfLot();
					String scale = "";
					if (scaleOfLot != null) {
						scale = scaleOfLot.getName();
					}

					newItem.getItemProperty(ColumnLabels.TAG.getName()).setValue(itemCheckBox);
					newItem.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(entryId);
					newItem.getItemProperty(ColumnLabels.DESIGNATION.getName()).setValue(desigButton);
					newItem.getItemProperty(ColumnLabels.LOT_LOCATION.getName()).setValue(location);
					newItem.getItemProperty(ColumnLabels.UNITS.getName()).setValue(scale);
					newItem.getItemProperty(ColumnLabels.AVAILABLE_INVENTORY.getName()).setValue(lotDetail.getAvailableLotBalance());
					newItem.getItemProperty(ColumnLabels.TOTAL.getName()).setValue(lotDetail.getActualLotBalance());
					newItem.getItemProperty(ColumnLabels.RESERVED.getName()).setValue(lotDetail.getReservedTotalForEntry());
					newItem.getItemProperty(ColumnLabels.NEWLY_RESERVED.getName()).setValue(0);
					newItem.getItemProperty(ColumnLabels.COMMENT.getName()).setValue(lotDetail.getCommentOfLot());

					String stockIds = lotDetail.getStockIds();
					Label stockIdsLbl = new Label(stockIds);
					stockIdsLbl.setDescription(stockIds);
					newItem.getItemProperty(ColumnLabels.STOCKID.getName()).setValue(stockIdsLbl);

					newItem.getItemProperty(ColumnLabels.LOT_ID.getName()).setValue(lotDetail.getLotId());
					newItem.getItemProperty(ColumnLabels.SEED_SOURCE.getName()).setValue(inventoryDetail.getSeedSource());
				}
			}
		}
	}

	protected GermplasmListData retrieveGermplasmListDataUsingLrecId(ListEntryLotDetails lotDetail) {
		try {
			return this.germplasmListManager.getGermplasmListDataByListIdAndLrecId(this.listId, lotDetail.getId());
		} catch (MiddlewareQueryException e) {
			ListManagerInventoryTable.LOG.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void toggleSelectOnLotEntries(CheckBox itemCheckBox) {
		if (((Boolean) itemCheckBox.getValue()).equals(true)) {
			this.listInventoryTable.select(itemCheckBox.getData());
		} else {
			this.listInventoryTable.unselect(itemCheckBox.getData());
		}
	}

	public void setDropHandler() {
		this.inventoryTableDropHandler =
				new InventoryTableDropHandler(this.listManagerMain, this.germplasmDataManager, this.germplasmListManager,
						this.inventoryDataManager, this.pedigreeService, this.crossExpansionProperties, this.listInventoryTable);
		if (this.enableDropHandler) {
			this.listInventoryTable.setDropHandler(this.inventoryTableDropHandler);
		}
	}

	public void setDragSource() {
		if (this.enableDragSource) {
			this.listInventoryTable.setDragMode(TableDragMode.ROW);
		}
	}

	public InventoryTableDropHandler getInventoryTableDropHandler() {
		return this.inventoryTableDropHandler;
	}

}
