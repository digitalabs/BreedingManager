package org.generationcp.breeding.manager.listmanager.util;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.constant.ColumnLabels;

public enum FillWithOption {
	
	FILL_WITH_PREFERRED_ID(ColumnLabels.PREFERRED_ID, Message.FILL_WITH_PREF_ID),
	FILL_WITH_PREFERRED_NAME(ColumnLabels.PREFERRED_NAME, Message.FILL_WITH_PREF_NAME),
	FILL_WITH_LOCATION(ColumnLabels.GERMPLASM_LOCATION, Message.FILL_WITH_LOCATION_NAME),
	FILL_WITH_GERMPLASM_DATE(ColumnLabels.GERMPLASM_DATE, Message.FILL_WITH_GERMPLASM_DATE),
	FILL_WITH_BREEDING_METHOD_NAME(ColumnLabels.BREEDING_METHOD_NAME, Message.FILL_WITH_BREEDING_METHOD_NAME),
	FILL_WITH_BREEDING_METHOD_ABBREV(ColumnLabels.BREEDING_METHOD_ABBREVIATION, Message.FILL_WITH_BREEDING_METHOD_ABBREVIATION),
	FILL_WITH_BREEDING_METHOD_NUMBER(ColumnLabels.BREEDING_METHOD_NUMBER, Message.FILL_WITH_BREEDING_METHOD_NUMBER),
	FILL_WITH_BREEDING_METHOD_GROUP(ColumnLabels.BREEDING_METHOD_GROUP, Message.FILL_WITH_BREEDING_METHOD_GROUP),
	FILL_WITH_CROSS_FEMALE_GID(ColumnLabels.CROSS_FEMALE_GID, Message.FILL_WITH_CROSS_FEMALE_GID),
	FILL_WITH_CROSS_FEMALE_NAME(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME, Message.FILL_WITH_CROSS_FEMALE_PREFERRED_NAME),
	FILL_WITH_CROSS_MALE_GID(ColumnLabels.CROSS_MALE_GID, Message.FILL_WITH_CROSS_MALE_GID),
	FILL_WITH_CROSS_MALE_NAME(ColumnLabels.CROSS_MALE_PREFERRED_NAME, Message.FILL_WITH_CROSS_MALE_PREFERRED_NAME),
	
	// the items below have no ColumnLabel associated as they are either parent options or used to fill up existing columns
	FILL_WITH_EMPTY(null, Message.FILL_WITH_EMPTY),
	FILL_WITH_CROSS_EXPANSION(null, Message.FILL_WITH_CROSS_EXPANSION),
	FILL_WITH_ATTRIBUTE(null, Message.FILL_WITH_ATTRIBUTE),
	FILL_WITH_SEQUENCE_NUMBMER(null, Message.FILL_WITH_SEQUENCE_NUMBER),
	FILL_WITH_BREEDING_METHOD_INFO(null, Message.FILL_WITH_BREEDING_METHOD_INFO),
	FILL_WITH_CROSS_FEMALE_INFO(null, Message.FILL_WITH_CROSS_FEMALE_INFORMATION),
	FILL_WITH_CROSS_MALE_INFO(null, Message.FILL_WITH_CROSS_MALE_INFORMATION);
	
	
	private ColumnLabels columnLabel;
	private Message messageKey;
	
	FillWithOption(final ColumnLabels columnLabel, final Message messageKey) {
		this.columnLabel = columnLabel;
		this.messageKey = messageKey;
	}
	
	public ColumnLabels getColumnLabel() {
		return columnLabel;
	}

	
	public Message getMessageKey() {
		return messageKey;
	}
	
}
