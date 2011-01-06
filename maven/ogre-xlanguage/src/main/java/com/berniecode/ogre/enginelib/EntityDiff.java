package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * Represents an update to a single {@link Entity}
 *
 * @author Bernie Sumption
 */
public class EntityDiff extends EntityValue {

	private final boolean[] isChanged;

	public EntityDiff(EntityType entityType, long id, Object[] values, boolean[] isChanged) {
		super(entityType, id, values);
		this.isChanged = isChanged;
	}

	/**
	 * @return An {@link EntityDiff} object that if applied to the entity <code>from</code> will
	 *         change its values to be equal to those of <code>to</code>
	 */
	public static EntityDiff build(RawPropertyValueSet from, RawPropertyValueSet to) {
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
			Object fromValue = from.getRawPropertyValue(property);
			Object toValue = to.getRawPropertyValue(property);
			if (!ValueUtils.valuesAreEquivalent(fromValue, toValue)) {
				if (toValue instanceof Entity) {
					changedValues[i] = ValueUtils.boxLong(((Entity) toValue).getEntityId());
				} else {
					changedValues[i] = toValue;
				}
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
	 * @see PartialRawPropertyValueSet#getPropertyValue(Property)
	 */
	public Object getRawPropertyValue(Property property) {
		if (!hasUpdatedValue(property)) {
			throw new OgreException(this + " has no value for " + property);
		}
		return super.getRawPropertyValue(property);
	}

	/**
	 * @see EntityDiff#hasUpdatedValue(Property)
	 */
	public boolean hasUpdatedValue(Property property) {
		return isChanged[property.getPropertyIndex()];
	}

	public String toString() {
		return "partial " + super.toString();
	}

}
