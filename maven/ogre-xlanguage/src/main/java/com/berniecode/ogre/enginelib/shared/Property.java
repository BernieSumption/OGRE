package com.berniecode.ogre.enginelib.shared;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public interface Property extends Named {

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
}
