package com.berniecode.ogre.enginelib.shared;



/**
 * A simple implementation of the {@link TypeDomain} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableTypeDomain implements TypeDomain {

	private final EntityType[] entityTypes;
	private final String typeDomainId;

	public ImmutableTypeDomain(String typeDomainId, EntityType[] entityTypes) {
		this.typeDomainId = typeDomainId;
		this.entityTypes = entityTypes;
	}

	public EntityType[] getEntityTypes() {
		return entityTypes;
	}

	public String getTypeDomainId() {
		return typeDomainId;
	}

}
