package com.berniecode.ogre.engine.shared;

/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of its entity type and
 * ID
 * 
 * @author Bernie Sumption
 */
public interface Entity {

	/**
	 * @return The type of this Entity
	 */
	EntityType getEntityType();

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	long getId();

	/**
	 * @return A collection of {@link PropertyValue} objects for this Entity, one for each
	 *         {@link Property} in the associated {@link EntityType}, and in the same order
	 */
	OrderedCollection getValues();
}
