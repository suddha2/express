package lk.express.bean;

import java.math.BigInteger;

import lk.express.Context;

/**
 * Interface for beans that has limited visibility based on allowed divisions
 */
public interface HasAllowedDivisions {

	static final int BITMASK_LENGTH = 50;

	BigInteger getAllowedDivisions();

	void setAllowedDivisions(BigInteger allowedDivisions);

	default BigInteger getWriteAllowedDivisions() {
		return getAllowedDivisions();
	}

	default void setWriteAllowedDivisions(BigInteger allowedDivisions) {
	}

	default void populateAllowedDivisions() {
		if (getAllowedDivisions() == null) {
			setAllowedDivisions(Context.getSessionData().getDivisionBitmask());
		}
		if (getWriteAllowedDivisions() == null) {
			setWriteAllowedDivisions(Context.getSessionData().getDivisionBitmask());
		}
	}
}
