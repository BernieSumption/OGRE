package com.berniecode.ogre.edr;

import java.util.List;

/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of
 * its entity type and ID
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
	 * @return The values of this Entity
	 */
	List<PropertyValue> getValues();
}
