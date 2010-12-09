package com.berniecode.ogre.enginelib.shared;

/**
 * The value of a single {@link Property} of an {@link Entity}
 * 
 * @author Bernie Sumption
 */
public class PropertyValue {

	private final Property property;
	private final Object value;
	
	public PropertyValue(Property property, Object value) {
		this.property = property;
		this.value = value;
	}

	/**
	 * @return The {@link Property} that this value is for
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return The value of this property
	 */
	public Object getValue() {
		return value;
	}
}
