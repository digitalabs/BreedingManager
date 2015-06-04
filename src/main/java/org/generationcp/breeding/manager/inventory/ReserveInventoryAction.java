
package org.generationcp.breeding.manager.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.ReservedInventoryKey;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ReserveInventoryAction implements Serializable {

	private static final long serialVersionUID = -6868930047867345575L;
	private static final Logger LOG = LoggerFactory.getLogger(ReserveInventoryAction.class);

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private UserDataManager userDataManager;

	@Resource
	private ContextUtil contextUtil;

	private Map<ListEntryLotDetails, Double> validLotReservations;
	private Map<ListEntryLotDetails, Double> invalidLotReservations;

	private final ReserveInventorySource source;

	private ReservationStatusWindow reservationStatus;

	public ReserveInventoryAction(ReserveInventorySource source) {
		super();
		this.source = source;
	}

	public void validateReservations(Map<ReservationRowKey, List<ListEntryLotDetails>> reservations) {

		// reset allocation
		this.validLotReservations = new HashMap<ListEntryLotDetails, Double>();
		this.invalidLotReservations = new HashMap<ListEntryLotDetails, Double>();

		Map<Integer, Double> duplicatedLots = this.getTotalReserveAmountPerLot(reservations);

		List<Integer> checkedLots = new ArrayList<Integer>();
		for (Map.Entry<ReservationRowKey, List<ListEntryLotDetails>> entry : reservations.entrySet()) {
			List<ListEntryLotDetails> lotList = entry.getValue();
			ReservationRowKey key = entry.getKey();
			Double amountReserved = key.getAmountToReserve();

			for (ListEntryLotDetails lot : lotList) {
				Double availBalance = lot.getAvailableLotBalance();

				if (checkedLots.contains(lot.getLotId())) {
					// duplicated lots mapped to GID that has multiple entries in list entries
					Double totalAmountReserved = duplicatedLots.get(lot.getLotId());
					if (availBalance < totalAmountReserved) {
						this.removeAllLotfromReservationLists(lot.getLotId());
						this.invalidLotReservations.put(lot, totalAmountReserved);
					} else {
						this.validLotReservations.put(lot, amountReserved);
					}
				} else if (availBalance < amountReserved) {
					this.invalidLotReservations.put(lot, amountReserved);
				} else {
					this.validLotReservations.put(lot, amountReserved);
				}
				// marked all checked lots
				checkedLots.add(lot.getLotId());
			}
		}

		boolean withInvalidReservations = false;
		if (!this.validLotReservations.isEmpty() && !this.invalidLotReservations.isEmpty()) {
			// if there is an invalid reservation
			this.reservationStatus = new ReservationStatusWindow(this.invalidLotReservations);
			this.source.addReservationStatusWindow(this.reservationStatus);
			withInvalidReservations = true;
		}

		this.source.updateListInventoryTable(this.validLotReservations, withInvalidReservations);
	}

	private void removeAllLotfromReservationLists(Integer lotId) {
		List<ListEntryLotDetails> lotDetails = new ArrayList<ListEntryLotDetails>();
		lotDetails.addAll(this.validLotReservations.keySet());
		lotDetails.addAll(this.invalidLotReservations.keySet());

		for (ListEntryLotDetails lot : lotDetails) {
			if (lot.getLotId() == lotId) {
				this.validLotReservations.remove(lot);
				this.invalidLotReservations.remove(lot);
			}
		}

	}

	private Map<Integer, Double> getTotalReserveAmountPerLot(Map<ReservationRowKey, List<ListEntryLotDetails>> reservations) {
		Map<Integer, Double> duplicatedLots = new HashMap<Integer, Double>();

		for (Map.Entry<ReservationRowKey, List<ListEntryLotDetails>> entry : reservations.entrySet()) {
			List<ListEntryLotDetails> lotList = entry.getValue();
			ReservationRowKey key = entry.getKey();
			Double amountReserved = key.getAmountToReserve();

			for (ListEntryLotDetails lot : lotList) {
				Integer lotId = lot.getLotId();
				if (duplicatedLots.containsKey(lotId)) {
					// sum up the reservations
					Double totalAmount = Double.valueOf(duplicatedLots.get(lotId)) + amountReserved;

					duplicatedLots.remove(lotId);
					duplicatedLots.put(lotId, totalAmount);
				} else {
					duplicatedLots.put(lotId, amountReserved);
				}
			}
		}

		return duplicatedLots;
	}

	public boolean saveReserveTransactions(Map<ListEntryLotDetails, Double> validReservationsToSave, Integer listId) {
		List<Transaction> reserveTransactionList = new ArrayList<Transaction>();
		try {
			for (Map.Entry<ListEntryLotDetails, Double> entry : validReservationsToSave.entrySet()) {
				ListEntryLotDetails lotDetail = entry.getKey();

				Integer lotId = lotDetail.getLotId();
				Integer transactionDate = DateUtil.getCurrentDateAsIntegerValue();
				Integer transacStatus = 0;

				// since this is a reserve transaction
				Double amountToReserve = -1 * entry.getValue();
				String comments = "";
				String sourceType = "LIST";
				Integer lrecId = lotDetail.getId();

				Double prevAmount = 0D;
				Integer ibdbUserId = this.contextUtil.getCurrentUserLocalId();
				Integer personId = this.userDataManager.getPersonById(ibdbUserId).getId();

				Transaction reserveTransaction = new Transaction();

				reserveTransaction.setUserId(ibdbUserId);

				Lot lot = new Lot(lotId);
				reserveTransaction.setLot(lot);

				reserveTransaction.setTransactionDate(transactionDate);
				reserveTransaction.setStatus(transacStatus);
				reserveTransaction.setQuantity(amountToReserve);
				reserveTransaction.setComments(comments);
				reserveTransaction.setCommitmentDate(transactionDate);
				reserveTransaction.setSourceType(sourceType);
				reserveTransaction.setSourceId(listId);
				reserveTransaction.setSourceRecordId(lrecId);
				reserveTransaction.setPreviousAmount(prevAmount);
				reserveTransaction.setPersonId(personId);

				reserveTransactionList.add(reserveTransaction);
			}

			this.inventoryDataManager.addTransactions(reserveTransactionList);

		} catch (MiddlewareQueryException e) {
			ReserveInventoryAction.LOG.error(e.getMessage(), e);
		}
		return true;
	}

	// SETTERS AND GETTERS
	public Map<ListEntryLotDetails, Double> getValidLotReservations() {
		return this.validLotReservations;
	}

	public Map<ListEntryLotDetails, Double> getInvalidLotReservations() {
		return this.invalidLotReservations;
	}

	public List<ReservedInventoryKey> getLotIdAndLrecId(List<ListEntryLotDetails> listEntries) {
		List<ReservedInventoryKey> lrecIds = new ArrayList<ReservedInventoryKey>();
		int id = 1;
		for (ListEntryLotDetails lotDetail : listEntries) {
			ReservedInventoryKey key = new ReservedInventoryKey(id, lotDetail.getId(), lotDetail.getLotId());
			if (!lrecIds.contains(key)) {
				lrecIds.add(key);
				id++;
			}
		}
		return lrecIds;
	}

	public void cancelReservations(List<ListEntryLotDetails> listEntries) throws MiddlewareQueryException {
		this.inventoryDataManager.cancelReservedInventory(this.getLotIdAndLrecId(listEntries));
	}
}
