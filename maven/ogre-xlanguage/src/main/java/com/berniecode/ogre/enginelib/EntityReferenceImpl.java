package com.berniecode.ogre.enginelib;



/**
 * Simple implementation of {@link EntityReference}
 *
 * @author Bernie Sumption
 */
public class EntityReferenceImpl implements EntityReference {

	private final EntityType entityType;
	private final long entityId;

	public EntityReferenceImpl(EntityType entityType, long id) {
		this.entityType = entityType;
		this.entityId = id;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public long getEntityId() {
		return entityId;
	}

	public String toString() {
		return entityType + "#" + entityId;
	}

}
