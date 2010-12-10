package com.berniecode.ogre.enginelib.shared;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public class EntityType {

	private final int index;
	private final String name;
	private final Property[] properties;

	public EntityType(int index, String name, Property[] properties) {
		this.index = index;
		this.name = name;
		this.properties = properties;
	}

	/**
	 * @return The index of this {@link EntityType} in its parent {@link TypeDomain}
	 */
	public int getEntityTypeIndex() {
		return index;
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
	public Property getProperty(int propertyIndex) {
		return properties[propertyIndex];
	}

	/**
	 * @return The number of {@link Property}s in this entity type.
	 */
	public int getPropertyCount() {
		return properties.length;
	}
	
	public String toString() {
		return name;
	}
}
