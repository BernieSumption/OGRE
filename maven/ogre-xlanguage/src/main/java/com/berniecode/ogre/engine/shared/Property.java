package com.berniecode.ogre.engine.shared;

/**
 * An individual property on an entity type. Property is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public interface Property {

	/**
	 * @return The name of this property. Property names should be lower case words separated by
	 *         underscores, e.g. "property_name". This will be transformed into the recommended
	 *         property naming convention in the client language, e.g. getPropertyName() for Java or
	 *         propertyName in ActionScript.
	 */
	String getName();

	/**
	 * @return The type of this property.
	 */
	PropertyType getPropertyType();

	// /**
	// * @return a value from this property's metadata map as a String
	// */
	// String getStringMetadataValue(String key);
	//	
	// /**
	// * @return a value from this property's metadata map as an integer
	// */
	// int getIntegerMetadataValue(String key);
	//	
	// /**
	// * @return a value from this property's metadata map as a boolean
	// */
	// boolean getBooleanMetadataValue(String key);
}
