package com.berniecode.ogre.enginelib.shared;

/**
 * A set of entityValues for an {@link ObjectGraph}, one for each {@link Entity}.
 * 
 * <p>
 * Unlike {@link ObjectGraph}, this is a simple value object suitable for serialisation and
 * transmission over a network
 * 
 * @author Bernie Sumption
 */
public class ObjectGraphValue {
	
	private final String typeDomainId;
	private final String objectGraphId;
	private final EntityValue[] entityValues;

	public ObjectGraphValue(String typeDomainId, String objectGraphId, EntityValue[] values) {
		this.typeDomainId = typeDomainId;
		this.objectGraphId = objectGraphId;
		this.entityValues = values;
	}

	public String getTypeDomainId() {
		return typeDomainId;
	}

	public String getObjectGraphId() {
		return objectGraphId;
	}

	public EntityValue[] getEntityValues() {
		return entityValues;
	}

}
