package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;


/**
 * A value for an Entity. The principle difference between {@link EntityValue} objects and
 * {@link Entity} objects is that {@link EntityValue}s store values for {@link ReferenceProperty}s
 * as integer ids, whereas {@link Entity}s store them as actual references to the appropriate Entity
 * object.
 * 
 * @author Bernie Sumption
 */
public class EntityValue implements EntityReference, PartialRawPropertyValueSet {

	private final EntityType entityType;
	private final long entityId;
	private final Object[] values;

	public EntityValue(EntityType entityType, long id, Object[] values) {
		this.entityType = entityType;
		this.entityId = id;
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
		return entityId;
	}

	/**
	 * @see PartialRawPropertyValueSet#hasUpdatedValue(Property)
	 */
	public boolean hasUpdatedValue(Property property) {
		return true;
	}

	/**
	 * @see PartialRawPropertyValueSet#getPropertyValue(Property)
	 */
	public Object getRawPropertyValue(Property property) {
		return values[property.getPropertyIndex()];
	}
	
	public String toString() {
		return "value for " + getEntityType() + "#" + getEntityId();
	}

	/**
	 * @return an array of values for this {@link Entity}. The returned array is safe to modify.
	 */
	Object[] copyValues() {
		return ValueUtils.cloneArray(values);
	}

	/**
	 * Set references to a specified Entity to null.
	 * 
	 * <p>The entity is located by identity, not by entityType and id
	 */
	void nullReferencesTo(EntityReference entity) {
		ReferenceProperty[] properties = entityType.getReferenceProperties();
		for (int i = 0; i < properties.length; i++) {
			ReferenceProperty property = properties[i];
			int propertyIndex = property.getPropertyIndex();
			Object value = values[propertyIndex];
			if (value == null) {
				continue;
			}
			if (property.getReferenceType() == entity.getEntityType()) {
				if (ValueUtils.unboxLong(value) == entity.getEntityId()) {
					values[propertyIndex] = null;
				}
			}
		}
	}

}
