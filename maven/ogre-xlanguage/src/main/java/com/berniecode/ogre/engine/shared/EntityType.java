package com.berniecode.ogre.engine.shared;

/**
 * A description of an entity. EntityType is to {@link Entity} as java.lang.Class is to
 * java.lang.Object.
 * 
 * @author Bernie Sumption
 */
public interface EntityType {

	/**
	 * @return The name of this entity type, typically a fully qualified class name.
	 */
	String getName();

	/**
	 * @return The properties of this entity type.
	 */
	OrderedCollection<Property> getProperties();
}
