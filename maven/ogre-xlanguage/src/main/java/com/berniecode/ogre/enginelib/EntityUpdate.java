package com.berniecode.ogre.enginelib;



/**
 * Contains new values for some or all of an {@link Entity}'s properties
 *
 * @author Bernie Sumption
 */
public interface EntityUpdate extends EntityReference {

	/**
	 * @return A single value.
	 */
	public Object getPropertyValue(Property property);

	/**
	 * Check whether this {@link EntityUpdate} has a new value for the property at the specified
	 * index.
	 */
	public boolean hasUpdatedValue(Property property);

	/**
	 * Whether this {@link EntityUpdate}'s reference properties refer to Entities or integer ids
	 */
	public boolean isWired();

}