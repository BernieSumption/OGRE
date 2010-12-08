package com.berniecode.ogre.enginelib.shared;

/**
 * An individual property on an entity type. {@link Property} is to {@link EntityType} as
 * java.lang.reflect.Method is to java.lang.Class.
 * 
 * @author Bernie Sumption
 */
public class Property implements Named {

	private String name;
	private PropertyType propertyType;

	public Property(String name, PropertyType propertyType) {
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

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The type of this property.
	 */
	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}
}
