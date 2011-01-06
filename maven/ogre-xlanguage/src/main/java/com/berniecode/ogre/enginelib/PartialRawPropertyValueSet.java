package com.berniecode.ogre.enginelib;

/**
 * A {@link RawPropertyValueSet} that only contains values for a subset of properties.
 * 
 * @author Bernie Sumption
 */
public interface PartialRawPropertyValueSet extends RawPropertyValueSet {

	/**
	 * Check whether this {@link PartialRawPropertyValueSet} has a new value for the property at the
	 * specified index.
	 * 
	 * <p>
	 * Calling {@link #getRawPropertyValue(Property)} with a {@link Property} for which
	 * {@link #hasUpdatedValue(Property)} returns false will throw an exception
	 */
	public boolean hasUpdatedValue(Property property);

}