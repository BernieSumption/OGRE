package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
//TODO make this EntityDiffMessage
public class EntityDiff {

	private final EntityType entityType;
	private final long id;
	private final Object[] values;
	private final boolean[] isChanged;

	//TODO make it entityTypeIndex
	public EntityDiff(EntityType entityType, long id, Object[] values, boolean[] changed) {
		this.entityType = entityType;
		this.id = id;
		this.values = values;
		this.isChanged = changed;
	}

	/**
	 * @return An {@link EntityDiff} object that if applied to the entity <code>from</code> will
	 *         change its values to be equal to those of <code>to</code>
	 */
	//TODO: tests, and EDR describing
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
	public long getId() {
		return id;
	}

	/**
	 * @return A single value .
	 */
	public Object getValue(int propertyIndex) {
		if (!hasUpdatedValue(propertyIndex)) {
			throw new OgreException(this + " has no value for property " + propertyIndex);
		}
		return values[propertyIndex];
	}

	/**
	 * @return an array of flags indicating whether the value with the same position in the
	 *         {@link #getValues()} array is an update.
	 */
	public boolean hasUpdatedValue(int propertyIndex) {
		return isChanged[propertyIndex];
	}

	public String toString() {
		return "EntityDiff for " + entityType + "#" + id;
	}

}
