package com.berniecode.ogre.enginelib;

/**
 * Represents a change to an object graph.
 * 
 * <p>
 * See the OGRE white paper for an overview of the function of this and other EDR classes.
 * 
 * @author Bernie Sumption
 */
public class GraphUpdate {

	private final TypeDomain typeDomain;
	private final String objectGraphId;
	private final RawPropertyValueSet[] entityCreates;
	private final PartialRawPropertyValueSet[] entityUpdates;
	private final EntityReference[] entityDeletes;
	private final int dataVersion;
	private final int dataVersionScheme;

	public GraphUpdate(TypeDomain typeDomain, String objectGraphId, int dataVersion, int dataVersionScheme,
			RawPropertyValueSet[] entityValues, PartialRawPropertyValueSet[] entityDiffs,
			EntityReference[] entityDeletes) {
		this.typeDomain = typeDomain;
		this.objectGraphId = objectGraphId;
		this.dataVersion = dataVersion;
		this.dataVersionScheme = dataVersionScheme;
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
	 * Together with the type domain, identifies the object graph that this message
	 * should be applied to
	 */
	public String getObjectGraphId() {
		return objectGraphId;
	}

	/**
	 * A counter that increments with each graph update to the same object graph. Graph updates must
	 * be applied in order to guarantee the integrity of the slave object graph.
	 */
	public int getDataVersion() {
		return dataVersion;
	}

	/**
	 * A unique id for the data version series.
	 */
	public int getDataVersionScheme() {
		return dataVersionScheme;
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
	 * @return {@link EntityReference}s for entities that have been removed
	 */
	public EntityReference[] getEntityDeletes() {
		return entityDeletes;
	}

	public String toString() {
		return "graph update #" + dataVersion + " for " + typeDomain.getTypeDomainId() + "/" + objectGraphId;
	}

}
