package com.berniecode.ogre.enginelib.shared;



/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class GraphUpdate {

	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final Entity[] entities;
	private final EntityDiff[] entityDiffs;
	private final EntityDelete[] entityDeletes;

	public GraphUpdate(TypeDomain typeDomain, String objectGraphId, Entity[] entities, EntityDiff[] entityDiffs, EntityDelete[] entityDeletes) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.entities = entities == null ? new Entity[0] : entities;
		this.entityDiffs = entityDiffs == null ? new EntityDiff[0] : entityDiffs;
		this.entityDeletes = entityDeletes == null ? new EntityDelete[0] : entityDeletes;
	}

	/**
	 * Together with {@link #getObjectGraphId()}, identifies the object graph that this message
	 * should be applied to
	 */
	public TypeDomain getTypeDomain() {
		return typeDomain;
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
