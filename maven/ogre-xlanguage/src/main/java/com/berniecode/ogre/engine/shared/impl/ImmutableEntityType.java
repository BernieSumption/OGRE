package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.ImmutableOrderedCollection;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.Property;

/**
 * A simple implementation of the {@link EntityType} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableEntityType implements EntityType {

	private final String name;
	private final OrderedCollection<Property> properties;

	public ImmutableEntityType(String name, OrderedCollection<Property> properties) {
		this.name = name;
		this.properties = new ImmutableOrderedCollection<Property>(properties);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OrderedCollection<Property> getProperties() {
		return properties;
	}

}
