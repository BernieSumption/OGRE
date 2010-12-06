package com.berniecode.ogre.enginelib.shared;

/**
 * A simple implementation of the {@link Entity} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableEntity implements Entity {

	private final EntityType entityType;
	private final long id;
	private final Object[] values;

	public ImmutableEntity(EntityType entityType, long id, Object[] values) {
		this.entityType = entityType;
		this.id = id;
		this.values = values;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public long getId() {
		return id;
	}

	public Object[] getValues() {
		return values;
	}
	
	public String toString() {
		return entityType.getName() + "#" + id;
	}

}
