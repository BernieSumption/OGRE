package com.berniecode.ogre.enginelib.shared;

/**
 * The value of a single {@link Property} of an {@link Entity}
 * 
 * @author Bernie Sumption
 */
public interface PropertyValue {

	/**
	 * @return The Property that this value is for
	 */
	Property getProperty();

	/**
	 * @return The value of this property
	 */
	Object getValue();
}
