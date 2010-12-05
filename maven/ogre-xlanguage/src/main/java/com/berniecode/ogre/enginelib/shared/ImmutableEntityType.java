package com.berniecode.ogre.enginelib.shared;


/**
 * A simple implementation of the {@link EntityType} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableEntityType implements EntityType {

	private final String name;
	private final Property[] properties;

	public ImmutableEntityType(String name, Property[] properties) {
		this.name = name;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public Property[] getProperties() {
		return properties;
	}
	
	public String toString() {
		return name;
	}

}
