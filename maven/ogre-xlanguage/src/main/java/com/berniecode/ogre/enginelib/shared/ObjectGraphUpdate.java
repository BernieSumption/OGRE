package com.berniecode.ogre.enginelib.shared;


/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class ObjectGraphUpdate {

	private final String typeDomainId;
	private final String objectGraphId;
	private final Entity[] entities;
	private final EntityDiff[] entityDiffs;
	private final EntityDelete[] entityDeletes;

	public ObjectGraphUpdate(String typeDomainId, String objectGraphId, Entity[] entities, EntityDiff[] entityDiffs, EntityDelete[] entityDeletes) {
		this.typeDomainId = typeDomainId;
		this.objectGraphId = objectGraphId;
		this.entities = entities == null ? new Entity[0] : entities;
		this.entityDiffs = entityDiffs == null ? new EntityDiff[0] : entityDiffs;
		this.entityDeletes = entityDeletes == null ? new EntityDelete[0] : entityDeletes;
	}

	/**
	 * Together with {@link #getObjectGraphId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public String getTypeDomainId() {
		return typeDomainId;
	}

	/**
	 * Together with {@link #getTypeDomainId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public String getObjectGraphId() {
		return objectGraphId;
	}

	/**
	 * @return {@link Entity}s that have been created or updated.
	 */
	public Entity[] getEntities() {
		return entities;
	}

	/**
	 * @return {@link EntityDiff}s for entities that have been updated
	 */
	public EntityDiff[] getEntityDiffs() {
		return entityDiffs;
	}

	/**
	 * @return {@link EntityDelete}s for entities that have been removed
	 */
	public EntityDelete[] getEntityDeletes() {
		return entityDeletes;
	}

}
