package com.berniecode.ogre.edr;

/**
 * An individual property on an entity type. Property is to {@link EntityType}
 * as java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public interface Property {

	/**
	 * @return The name of this property. Property names should be lower case
	 *         words separated by underscores, e.g. "property_name". This will
	 *         be transformed into the recommended property naming convention in
	 *         the client language, e.g. getPropertyName() for Java or
	 *         propertyName in ActionScript.
	 */
	String getName();

	/**
	 * @return The type of this property.
	 */
	PropertyType getPropertyType();

	/**
	 * Get a value from this property's metadata map.
	 * 
	 * @param key
	 *            the key to fetch, e.g. "bitlength" for the integer property
	 *            type.
	 */
	<T> T getMetadataValue(MetadataKey<T> key);

	/**
	 * @return the EntityType that this property belongs to
	 */
	EntityType getEntityType();

	/**
	 * @return the position of this property in the parent {@link EntityType}'s
	 *         properties list
	 */
	int getPropertyIndex();
}
