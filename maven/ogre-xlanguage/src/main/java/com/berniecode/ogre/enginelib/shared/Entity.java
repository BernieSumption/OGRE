package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;


/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of its entity type and
 * ID.
 * 
 * @author Bernie Sumption
 */
public class Entity implements EntityReference, EntityUpdate {

	private final EntityType entityType;
	private final long id;
	private final Object[] values;

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
	public long getEntityId() {
		return id;
	}

	/**
	 * @return The value for a property of this {@link Entity}
	 */
	public Object getPropertyValue(Property property) {
		return values[property.getPropertyIndex()];
	}
	
	public String toString() {
		return entityType + "#" + id;
	}

	public Object[] copyValues() {
		return ValueUtils.cloneArray(values);
	}

	public int getEntityTypeIndex() {
		return entityType.getEntityTypeIndex();
	}

	public void update(EntityUpdate update) {
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (update.hasUpdatedValue(property)) {
				values[i] = update.getPropertyValue(property);
			}
		}
	}

	public boolean hasUpdatedValue(Property property) {
		return true;
	}
}
