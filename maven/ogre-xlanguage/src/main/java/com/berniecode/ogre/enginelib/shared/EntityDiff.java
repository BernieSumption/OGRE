package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiff {

	private final EntityType entityType;
	private final long id;
	private final Object[] values;
	private final boolean[] isChanged;

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
		if (from.getEntityType() != to.getEntityType()) {
			throw new OgreException("Can't build an EntityDiff from " + from + " to " + to + " because their entityTypes are different");
		}
		Property[] properties = from.getEntityType().getProperties();
		Object[] changedValues = new Object[properties.length];
		boolean[] changed = new boolean[properties.length];
		boolean anyChanged = false;
		for (int i=0; i<properties.length; i++) {
			Property property = properties[i];
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
		return new EntityDiff(from.getEntityType(), from.getId(), changedValues, changed);
	}

	/**
	 * Create a new {@link Entity} that is the result of applying these differences to another Entity.
	 * 
	 * <p>The target entity is not modified
	 */
	public Entity apply(Entity target) {
		Property[] properties = target.getEntityType().getProperties();
		Object[] newValues = new Object[properties.length];
		for (int i=0; i<properties.length; i++) {
			if (isChanged[i]) {
				newValues[i] = values[i];
			} else {
				newValues[i] = target.getPropertyValue(properties[i]);
			}
		}
		return new Entity(entityType, id, newValues);
	}

	/**
	 * @return The type of this Entity
	 */
	public EntityType getEntityType() {
		return entityType;
	}
//
//	public void setEntityType(EntityType entityType) {
//		this.entityType = entityType;
//	}
//
	/**
	 * @return The ID of this entity, unique within the scope of its entity type
	 */
	public long getId() {
		return id;
	}
//
//	public void setId(long id) {
//		this.id = id;
//	}
//
//	/**
//	 * @return An array of updated values for this Entity.
//	 */
//	public PropertyValue[] getValues() {
//		return values;
//	}
//
//	public void setValues(PropertyValue[] values) {
//		this.values = values;
//	}

	public String toString() {
		return "EntityDiff for " + entityType + "#" + id;
	}

}
