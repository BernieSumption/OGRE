package com.berniecode.ogre.enginelib.shared;


/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of its entity type and
 * ID.
 * 
 * @author Bernie Sumption
 */
public class Entity {

	private final EntityType entityType;
	private final long id;
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

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The value for a property of this {@link Entity}
	 */
	public Object getPropertyValue(Property property) {
		return values[property.getPropertyIndex()];
	}

	public void setValues(Object[] values) {
		this.values = values;
	}
	
	public String toString() {
		return entityType + "#" + id;
	}
}
