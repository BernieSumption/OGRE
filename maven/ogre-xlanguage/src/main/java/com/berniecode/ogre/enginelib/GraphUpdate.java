package com.berniecode.ogre.enginelib;



/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class GraphUpdate {

	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final RawPropertyValueSet[] entityCreates;
	private final PartialRawPropertyValueSet[] entityUpdates;
	private final EntityReference[] entityDeletes;

	public GraphUpdate(TypeDomain typeDomain, String objectGraphId, RawPropertyValueSet[] entityValues, PartialRawPropertyValueSet[] entityDiffs, EntityReference[] entityDeletes) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.entityCreates = entityValues == null ? new RawPropertyValueSet[0] : entityValues;
		this.entityUpdates = entityDiffs == null ? new PartialRawPropertyValueSet[0] : entityDiffs;
		this.entityDeletes = entityDeletes == null ? new EntityReference[0] : entityDeletes;
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
	 * @return {@link RawPropertyValueSet}s for Entities that have been been created
	 */
	public RawPropertyValueSet[] getEntityCreates() {
		return entityCreates;
	}

	/**
	 * @return {@link PartialRawPropertyValueSet}s for entities that have been updated
	 */
	public PartialRawPropertyValueSet[] getEntityUpdates() {
		return entityUpdates;
	}

	/**
	 * @return {@link EntityDelete}s for entities that have been removed
	 */
	public EntityReference[] getEntityDeletes() {
		return entityDeletes;
	}

}
