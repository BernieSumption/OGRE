package com.berniecode.ogre.enginelib.shared;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public class EntityType implements Named {

	private final String name;
	private final Property[] properties;

	public EntityType(String name, Property[] properties) {
		this.name = name;
		this.properties = properties;
	}

	/**
	 * @return The name of this entity type, typically a fully qualified class name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The {@link Property}s of this entity type.
	 */
	//TODO use immutable accessor
	public Property[] getProperties() {
		return properties;
	}
	
	public String toString() {
		return name;
	}
}
