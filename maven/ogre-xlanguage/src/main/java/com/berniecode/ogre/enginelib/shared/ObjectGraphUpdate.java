package com.berniecode.ogre.enginelib.shared;


/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class ObjectGraphUpdate {

	private final String typeDomainId;
	private final String objectGraphId;
	private final EntityValueMessage[] entities; //TODO make these Entity objects
	private final EntityDiffMessage[] entityDiffs; //TODO make these EntityDiff objects, and wire up EntityType correctly
	private final EntityDeleteMessage[] entityDeletes; //TODO make these EntityDelete, and wire up EntityType correctly

	public ObjectGraphUpdate(String typeDomainId, String objectGraphId, EntityValueMessage[] entities, EntityDiffMessage[] entityDiffs, EntityDeleteMessage[] entityDeletes) {
		this.typeDomainId = typeDomainId;
		this.objectGraphId = objectGraphId;
		this.entities = entities;
		this.entityDiffs = entityDiffs;
		this.entityDeletes = entityDeletes;
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
	public EntityValueMessage[] getEntityValues() {
		return entities;
	}

	/**
	 * @return {@link EntityDiffMessage}s for entities that have been updated
	 */
	public EntityDiffMessage[] getEntityDiffs() {
		return entityDiffs;
	}

	/**
	 * @return {@link EntityDeleteMessage}s for entities that have been removed
	 */
	public EntityDeleteMessage[] getEntityDeletes() {
		return entityDeletes;
	}

}
