package com.berniecode.ogre.enginelib.shared;

/**
 * The type of a property.
 * 
 * @author Bernie Sumption
 */
//TODO merge Property and PropertyType, all I do is endless property.getPropertyType().something
public interface PropertyType {

	/**
	 * @return A text description of this property type, including metadata, e.g. "32 bit integer"
	 */
	String getDescription();

}
