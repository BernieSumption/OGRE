package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.Property;
import com.berniecode.ogre.engine.shared.PropertyType;

/**
 * A simple implementation of the {@link Property} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableProperty implements Property {

	private final String name;
	private final PropertyType propertyType;

	public ImmutableProperty(String name, PropertyType propertyType) {
		this.name = name;
		this.propertyType = propertyType;
	}

	public String getName() {
		return name;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

}
