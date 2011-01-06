package com.berniecode.ogre.enginelib;



/**
 * Represents a change to an object graph.
 * 
 * @author Bernie Sumption
 */
public class GraphUpdate {

	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final RawPropertyValueSet[] entityValues;
	private final PartialRawPropertyValueSet[] entityDiffs;
	private final EntityDelete[] entityDeletes;

	public GraphUpdate(TypeDomain typeDomain, String objectGraphId, RawPropertyValueSet[] entityValues, EntityDiff[] entityDiffs, EntityDelete[] entityDeletes) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.entityValues = entityValues == null ? new RawPropertyValueSet[0] : entityValues;
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
	 * @return {@link EntityValue}s for Entities that have been been created or updated.
	 */
	//TODO change to getCompleteEntityValues
	public RawPropertyValueSet[] getEntityValues() {
		return entityValues;
	}

	/**
	 * @return {@link EntityDiff}s for entityValues that have been updated
	 */
	//TODO change to getPartialEntityValues
	public PartialRawPropertyValueSet[] getEntityDiffs() {
		return entityDiffs;
	}

	/**
	 * @return {@link EntityDelete}s for entityValues that have been removed
	 */
	public EntityDelete[] getEntityDeletes() {
		return entityDeletes;
	}

}
