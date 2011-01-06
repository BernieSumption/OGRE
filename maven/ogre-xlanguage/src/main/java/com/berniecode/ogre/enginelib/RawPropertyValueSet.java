package com.berniecode.ogre.enginelib;

/**
 * A complete set of values for an {@link Entity}
 * 
 * @author Bernie Sumption
 */
//TODO look for opportunities to replace concrete references with this and PartialRawPropertyValueSet
public interface RawPropertyValueSet extends EntityReference {

	/**
	 * @return Get the value of a single property. Values for {@link ReferenceProperty}s will be
	 *         returned as IDs, not as {@link Entity} objects.
	 */
	public Object getRawPropertyValue(Property property);

}
