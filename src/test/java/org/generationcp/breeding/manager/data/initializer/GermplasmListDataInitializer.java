
package org.generationcp.breeding.manager.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.inventory.ListDataInventory;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;

public class GermplasmListDataInitializer {

	public static GermplasmList createGermplasmList(final int id) {
		final GermplasmList germplasmList = new GermplasmList();
		germplasmList.setId(id);
		germplasmList.setName("List 001");
		germplasmList.setDescription("List 001 Description");
		germplasmList.setDate(20150101L);

		return germplasmList;
	}

	public static GermplasmList createGermplasmListWithListData(final int id, final int noOfEntries) {
		final GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setListData(createGermplasmListData(noOfEntries));

		return germplasmList;
	}

	public static GermplasmList createGermplasmListWithListDataAndInventoryInfo(final int id, final int noOfEntries) {
		final GermplasmList germplasmList = createGermplasmList(id);
		germplasmList.setListData(createGermplasmListDataWithInventoryInfo(noOfEntries));
		return germplasmList;
	}

	public static List<GermplasmListData> createGermplasmListData(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItem(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	public static List<GermplasmListData> createGermplasmListDataWithInventoryInfo(final Integer itemNo) {
		final List<GermplasmListData> listEntries = new ArrayList<GermplasmListData>();
		for (int i = 1; i <= itemNo; i++) {
			final GermplasmListData listEntry = createGermplasmListDataItemWithInventoryInfo(i);
			listEntries.add(listEntry);
		}

		return listEntries;
	}

	protected static GermplasmListData createGermplasmListDataItem(int i) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(i);
		listEntry.setDesignation("Designation " + i);
		listEntry.setEntryCode("EntryCode " + i);
		listEntry.setEntryId(i);
		listEntry.setGroupName("GroupName " + i);
		listEntry.setStatus(1);
		listEntry.setSeedSource("SeedSource " + i);
		listEntry.setGid(i);
		return listEntry;
	}

	protected static GermplasmListData createGermplasmListDataItemWithInventoryInfo(int i) {
		final GermplasmListData listEntry = new GermplasmListData();
		listEntry.setId(i);
		listEntry.setDesignation("Designation " + i);
		listEntry.setEntryCode("EntryCode " + i);
		listEntry.setEntryId(i);
		listEntry.setGroupName("GroupName " + i);
		listEntry.setStatus(1);
		listEntry.setSeedSource("SeedSource " + i);
		listEntry.setGid(i);
		listEntry.setInventoryInfo(createInventoryInfo(i, i, i));
		return listEntry;
	}

	protected static ListDataInventory createInventoryInfo(int itemNo, int listDataId, int gid) {

		ListDataInventory listDataInventory = new ListDataInventory(listDataId, gid);
		listDataInventory.setLotCount(0);
		listDataInventory.setActualInventoryLotCount(0);
		listDataInventory.setLotRows(new ArrayList<LotDetails>());
		listDataInventory.setReservedLotCount(0);
		listDataInventory.setStockIDs("SID:" + itemNo);

		return listDataInventory;
	}
}