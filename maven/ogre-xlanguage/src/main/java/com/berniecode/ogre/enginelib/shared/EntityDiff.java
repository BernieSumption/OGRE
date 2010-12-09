package com.berniecode.ogre.enginelib.shared;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiff {

	private EntityType entityType;
	private long id;
	private PropertyValue[] values;

	public EntityDiff(EntityType entityType, long id, PropertyValue[] values) {
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
	 * @return An array of updated values for this Entity.
	 */
	public PropertyValue[] getValues() {
		return values;
	}

	public void setValues(PropertyValue[] values) {
		this.values = values;
	}

	public String toString() {
		return "EntityDiff for " + entityType + "#" + id;
	}

}
