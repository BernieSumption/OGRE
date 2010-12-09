package com.berniecode.ogre.enginelib.shared;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public class Property implements Named {

	private final String name;
	private final PropertyType propertyType;
	private final int propertyIndex;

	public Property(String name, PropertyType propertyType, int propertyIndex) {
		this.name = name;
		this.propertyType = propertyType;
		this.propertyIndex = propertyIndex;
	}

	/**
	 * @return The name of this property. Property names should be lower case words separated by
	 *         underscores, e.g. "property_name". This will be transformed into the recommended
	 *         property naming convention in the client language, e.g. getPropertyName() for Java or
	 *         propertyName in ActionScript.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The type of this property.
	 */
	public PropertyType getPropertyType() {
		return propertyType;
	}

	/**
	 * The position of this property in the parent {@link EntityType}'s properties array.
	 */
	public int getPropertyIndex() {
		return propertyIndex;
	}
}
