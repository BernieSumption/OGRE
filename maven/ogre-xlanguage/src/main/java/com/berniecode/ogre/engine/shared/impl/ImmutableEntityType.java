package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.ImmutableOrderedCollection;
import com.berniecode.ogre.engine.shared.OrderedCollection;

/**
 * A simple implementation of the {@link EntityType} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableEntityType implements EntityType {

	private final String name;
	private final OrderedCollection properties;

	public ImmutableEntityType(String name, OrderedCollection properties) {
		this.name = name;
		this.properties = new ImmutableOrderedCollection(properties);
	}

	public String getName() {
		return name;
	}

	public OrderedCollection getProperties() {
		return properties;
	}

}
