package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiffMessage implements EntityReference, EntityUpdate {

	private final int entityTypeIndex;
	private final long entityId;
	private final Object[] values;
	private final boolean[] isChanged;

	public EntityDiffMessage(int entityTypeIndex, long id, Object[] values, boolean[] isChanged) {
		this.entityTypeIndex = entityTypeIndex;
		this.entityId = id;
		this.values = values;
		this.isChanged = isChanged;
	}

	/**
	 * @return An {@link EntityDiffMessage} object that if applied to the entity <code>from</code> will
	 *         change its values to be equal to those of <code>to</code>
	 */
	public static EntityDiffMessage build(Entity from, Entity to) {
		EntityType entityType = from.getEntityType();
		if (entityType != to.getEntityType()) {
			throw new OgreException("Can't build an EntityDiffMessage from " + from + " to " + to + " because their entityTypes are different");
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
		return new EntityDiffMessage(entityType.getEntityTypeIndex(), from.getEntityId(), changedValues, changed);
	}

	/**
	 * @return The type of this Entity
	 */
	public int getEntityTypeIndex() {
		return entityTypeIndex;
	}

	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getEntityId() {
		return entityId;
	}

	/**
	 * @see EntityUpdate#getValue(int)
	 */
	public Object getValue(int propertyIndex) {
		if (!hasUpdatedValue(propertyIndex)) {
			throw new OgreException(this + " has no value for property " + propertyIndex);
		}
		return values[propertyIndex];
	}

	/**
	 * @see EntityUpdate#hasUpdatedValue(int)
	 */
	public boolean hasUpdatedValue(int propertyIndex) {
		return isChanged[propertyIndex];
	}

	public String toString() {
		return "EntityDiffMessage for entity " + entityTypeIndex + "#" + entityId;
	}

}
