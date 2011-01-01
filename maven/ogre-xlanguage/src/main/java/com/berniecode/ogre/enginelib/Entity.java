package com.berniecode.ogre.enginelib;

import com.berniecode.ogre.enginelib.platformhooks.OgreException;
import com.berniecode.ogre.enginelib.platformhooks.ValueUtils;

/**
 * A single object in an {@link ObjectGraph}, uniquely identified by a tuple of its entity type and
 * ID.
 * 
 * <p>
 * Entities can exist in two states: <em>wired</em> and <em>unwired</em>. When the Entity is first
 * created it is constructed, it has numerical values for its reference properties, so calling
 * <code>entity.getPropertyValue(someReferenceProperty)</code> will return the ID of the entity
 * referenced by the property value.
 * 
 * <p>
 * When the Entity is imported into ClientEngine, it is converted into a wired entity by calling
 * {@link #wireEntityReferences(EntityStore, Entity[])}. This replaces the integer ids with actual
 * references to Entities.
 * 
 * @author Bernie Sumption
 */
public class Entity implements EntityReference, EntityUpdate {
	
	private boolean isWired = false;

	private final EntityType entityType;
	private final long id;
	private final Object[] values;
	
	/**
	 * Create an unwired Entity
	 */
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

	public boolean hasUpdatedValue(Property property) {
		return true;
	}
	
	//
	// OGRE INTERNAL API
	//


	/**
	 * @return an array of values for this {@link Entity}. The returned array is safe to modify.
	 */
	Object[] copyValues() {
		return ValueUtils.cloneArray(values);
	}

	/**
	 * Modify this {@link Entity} with data from an {@link EntityUpdate} instance
	 */
	void update(EntityUpdate update) {
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			if (update.hasUpdatedValue(property)) {
				values[i] = update.getPropertyValue(property);
			}
		}
	}

	/**
	 * Modify this {@link Entity} with data from an array. Each position in the array will be
	 * interpreted as a propertyIndex
	 */
	void updateFromArray(Object[] update) {
		for (int i = 0; i < update.length; i++) {
			values[i] = update[i];
		}
	}

	/**
	 * The values array passed to the constructor of this class contains integers instead of Entity
	 * references, so if property #0 is a "reference to Foo" property referencing Foo#7,
	 * getPropertyValue(property0) would return the number "7".
	 * 
	 * <p>
	 * This method is used to provide a set of Entities to resolve references in, so that
	 * getPropertyValue(property0) returns the actual Entity Foo#7
	 * 
	 * <p>
	 * Entities are resolved first in the EntityStore, then in the array of entities if they are not
	 * found in the store
	 * 
	 * @private
	 */
	void wireEntityReferences(EntityStore store, Entity[] array) {
		if (isWired) {
			throw new OgreException("wireEntityReferences() has already been called on " + this);
		}
		isWired = true;
		Property[] properties = entityType.getReferenceProperties();
		for (int i = 0; i < properties.length; i++) {
			ReferenceProperty property = (ReferenceProperty) properties[i];
			EntityType refType = property.getReferenceType();
			long refId = ValueUtils.unboxLong((Long) values[property.getPropertyIndex()]);
			Entity entity = store.get(refType, refId);
			if (entity == null) {
				for (int j = 0; j < array.length; j++) {
					if (array[j].getEntityType() == refType && array[j].getEntityId() == refId) {
						entity = array[j];
					}
				}
			}
			if (entity == null) {
				throw new OgreException("Property '" + property + "' of entity type " + refType + " references non-existant entity " + refType + "#" + refId);
			}
			values[property.getPropertyIndex()] = entity;
		}
	}
}