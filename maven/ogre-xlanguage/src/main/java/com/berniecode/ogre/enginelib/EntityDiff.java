package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiff implements EntityReference, EntityUpdate {

	private final EntityType entityType;
	private final long entityId;
	private final Object[] values;
	private final boolean[] isChanged;

	public EntityDiff(EntityType entityType, long id, Object[] values, boolean[] isChanged) {
		this.entityType = entityType;
		this.entityId = id;
		this.values = values;
		this.isChanged = isChanged;
	}

	/**
	 * @return An {@link EntityDiff} object that if applied to the entity <code>from</code> will
	 *         change its values to be equal to those of <code>to</code>
	 */
	public static EntityDiff build(Entity from, Entity to) {
		EntityType entityType = from.getEntityType();
		if (entityType != to.getEntityType()) {
			throw new OgreException("Can't build an EntityDiff from " + from + " to " + to + " because their entityTypes are different");
		}
		int propertyCount = entityType.getPropertyCount();
		Object[] changedValues = new Object[propertyCount];
		boolean[] changed = new boolean[propertyCount];
		boolean anyChanged = false;
		for (int i=0; i<propertyCount; i++) {
			Property property = entityType.getProperty(i);
			Object fromValue = from.getPropertyValue(property);
			Object toValue = to.getPropertyValue(property);
			if (!ValueUtils.valuesAreEquivalent(fromValue, toValue)) {
				changedValues[i] = toValue;
				changed[i] = true;
				anyChanged = true;
			}
		}
		if (!anyChanged) {
			return null;
		}
		return new EntityDiff(entityType, from.getEntityId(), changedValues, changed);
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
	 * @see EntityUpdate#getPropertyValue(Property)
	 */
	public Object getPropertyValue(Property property) {
		if (!hasUpdatedValue(property)) {
			throw new OgreException(this + " has no value for " + property);
		}
		return values[property.getPropertyIndex()];
	}

	/**
	 * @see EntityUpdate#hasUpdatedValue(Property)
	 */
	public boolean hasUpdatedValue(Property property) {
		return isChanged[property.getPropertyIndex()];
	}

	public String toString() {
		return "EntityDiff for entity " + entityType + "#" + entityId;
	}

}
