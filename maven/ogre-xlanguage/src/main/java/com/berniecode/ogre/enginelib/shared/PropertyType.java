package com.berniecode.ogre.enginelib.shared;

/**
 * The type of a property.
 * 
 * @author Bernie Sumption
 */
public interface PropertyType {

	/**
	 * @return The typeCode of this property type as per the OGRE wire format specification
	 */
	int getTypecode();

	/**
	 * @return A text description of this property type, including metadata, e.g. "32 bit integer"
	 */
	String getDescription();

}
