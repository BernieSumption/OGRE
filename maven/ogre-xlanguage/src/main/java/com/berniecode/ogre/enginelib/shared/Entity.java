package com.berniecode.ogre.enginelib.shared;

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
	 * @return An array of values for this Entity, one for each {@link Property} in the associated
	 *         {@link EntityType}, and in the same order. The returned array is not safe to modify.
	 *         It must be copied before being passed outside of OGRE.
	 */
	Object[] getValues();
}
