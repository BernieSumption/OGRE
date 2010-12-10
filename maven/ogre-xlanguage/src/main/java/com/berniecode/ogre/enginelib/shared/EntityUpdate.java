package com.berniecode.ogre.enginelib.shared;

public interface EntityUpdate {

	/**
	 * @return A single value .
	 */
	public abstract Object getValue(int propertyIndex);

	/**
	 * @return an array of flags indicating whether the value with the same position in the
	 *         {@link #getValues()} array is an update.
	 */
	public abstract boolean hasUpdatedValue(int propertyIndex);

}