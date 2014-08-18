package org.generationcp.breeding.manager.inventory;

public class ReservationRowKey {
	String scale;
	Double amountToReserve;
	public ReservationRowKey(String scale, Double amountToReserve) {
		super();
		this.scale = scale;
		this.amountToReserve = amountToReserve;
	}
	public String getScale() {
		return scale;
	}
	public void setScale(String scale) {
		this.scale = scale;
	}
	public Double getAmountToReserve() {
		return amountToReserve;
	}
	public void setAmountToReserve(Double amountToReserve) {
		this.amountToReserve = amountToReserve;
	}
}