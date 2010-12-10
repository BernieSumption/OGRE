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
public class ObjectGraphValueMessage {
	
	private final String typeDomainId;
	private final String objectGraphId;
	private final EntityValueMessage[] entityValues;

	public ObjectGraphValueMessage(String typeDomainId, String objectGraphId, EntityValueMessage[] values) {
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

	public EntityValueMessage[] getEntityValues() {
		return entityValues;
	}

}
