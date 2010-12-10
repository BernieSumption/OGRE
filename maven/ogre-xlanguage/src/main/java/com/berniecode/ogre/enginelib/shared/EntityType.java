package com.berniecode.ogre.enginelib.shared;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
//TODO try making this immutable
public class EntityType implements Named {

	private String name;
	private Property[] properties;

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

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The {@link Property}s of this entity type.
	 */
	public Property[] getProperties() {
		return properties;
	}

	public void setProperties(Property[] properties) {
		this.properties = properties;
	}
	
	public String toString() {
		return name;
	}
}
