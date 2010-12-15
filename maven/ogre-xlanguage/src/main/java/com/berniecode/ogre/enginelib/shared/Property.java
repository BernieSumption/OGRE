package com.berniecode.ogre.enginelib.shared;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public class Property {

	private final int propertyIndex;
	private final String name;
	private final PropertyType propertyType;

	public Property(int propertyIndex, String name, PropertyType propertyType) {
		this.propertyIndex = propertyIndex;
		this.name = name;
		this.propertyType = propertyType;
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
	
	public String toString() {
		return propertyType.getDescription() + " property " + name; //TODO use this in EDRDescriber
	}
}
