package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;

/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of its entity type and
 * ID.
 * 
 * @author Bernie Sumption
 */
public class Entity {

	private EntityType entityType;
	private long id;
	private Object[] values;

	public Entity(EntityType entityType, long id, Object[] values) {
		this.entityType = entityType;
		this.id = id;
		this.values = values;
	}

	/**
	 * @return The type of this Entity
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return An array of values for this Entity, one for each {@link Property} in the associated
	 *         {@link EntityType}, and in the same order. The returned array is not safe to modify.
	 *         It must be copied before being passed outside of OGRE.
	 */
	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	void merge(Entity sourceEntity) {
		throw new OgreException("Entity merging is not implemented yet");
	}
}
