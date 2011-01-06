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
//TODO update these docs
public class Entity implements EntityReference, RawPropertyValueSet {

	/**
	 * The minimum permitted ID
	 */
	public static final long MIN_ID = 1L;
	
	/**
	 * The maximum permitted ID is 2^52. Higher IDs can't be unambiguously represented as double
	 * precision floating point numbers, and some clients must use that representation as the host
	 * language has no native long integer
	 */
	//TODO verify this number
	public static final long MAX_ID = 0x000FFFFFFFFFFFFFL; 

	private final EntityType entityType;
	private final long id;
	private final Object[] values;

	/**
	 * Constructor
	 * 
	 * This constructor permits the creation of Entities without values, which is necessary in order
	 * to allow circular references.
	 */
	public Entity(EntityType entityType, long id, Object[] initialValues) {
		if (id < 1) {
			throw new OgreException("IDs must be positive integers between 1 and 2^52");
		}
		if (id > MAX_ID) {
			OgreLog.warn("Entity " + entityType + "#" + id + " has an ID higer than 2^52, and may cause undefined bahaviour on client languages with no long integer type");
		}
		this.entityType = entityType;
		this.id = id;
		this.values = new Object[entityType.getPropertyCount()];
		if (initialValues != null) {
			updateFromArray(initialValues);
		}
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
	
	//
	// OGRE INTERNAL API
	//

	/**
	 * Modify this {@link Entity} with data from an {@link RawPropertyValueSet} instance
	 * 
	 * <p>
	 * {@link Entity} references will be resolved first from the supplied {@link EntityStore}, then
	 * from the array of entities
	 */
	void update(RawPropertyValueSet update, EntityStore store, Entity[] array) {
		boolean isPartial = update instanceof PartialRawPropertyValueSet;
		for (int i=0; i<entityType.getPropertyCount(); i++) {
			Property property = entityType.getProperty(i);
			boolean hasUpdatedValue = true;
			if (isPartial) {
				hasUpdatedValue = ((PartialRawPropertyValueSet) update).hasUpdatedValue(property);
			}
			if (hasUpdatedValue) {
				Object value = update.getRawPropertyValue(property);
				// resolve Entity references
				if (value != null && property instanceof ReferenceProperty) {
					EntityType refType = ((ReferenceProperty) property).getReferenceType();
					long refId = ValueUtils.objectToId(value);
					value = getEntity(refType, refId, store, array);
					if (value == null) {
						throw new OgreException("Property '" + property + "' of entity type " + property.getEntityType() + " references non-existant entity " + refType + "#" + refId);
					}
				}
				ValueUtils.validatePropertyValue(property, value, true);
				values[i] = value;
			}
		}
	}

	private Object getEntity(EntityType refType, long refId, EntityStore store, Entity[] array) {
		Entity entity = null;
		entity = store.get(refType, refId);
		if (entity == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].getEntityType() == refType && array[i].getEntityId() == refId) {
					entity = array[i];
				}
			}
		}
		return entity;
	}

	/**
	 * Modify this {@link Entity} with data from an array. Each position in the array will be
	 * interpreted as a propertyIndex
	 */
	void updateFromArray(Object[] update) {
		for (int i = 0; i < update.length; i++) {
			ValueUtils.validatePropertyValue(entityType.getProperty(i), update[i], true);
			values[i] = update[i];
		}
	}

	/**
	 * Set references to a specified Entity to null. This is used to maintain referential integrity
	 * when an entity is removed from an object graph
	 * 
	 * <p>The entity is located by identity, not by entityType and id
	 */
	void nullReferencesTo(EntityReference entity) {
		ReferenceProperty[] properties = entityType.getReferenceProperties();
		for (int i = 0; i < properties.length; i++) {
			ReferenceProperty property = properties[i];
			Object value = values[property.getPropertyIndex()];
			if (value == null) {
				continue;
			}
			if (property.getReferenceType() == entity.getEntityType()) {
				if (((Entity) value).getEntityId() == entity.getEntityId()) {
					values[property.getPropertyIndex()] = null;
				}
			}
		}
	}

	public Object getRawPropertyValue(Property property) {
		Object value = getPropertyValue(property);
		if (value != null && property instanceof ReferenceProperty) {
			return ValueUtils.idToObject(((Entity) value).getEntityId());
		}
		return value;
	}

}
