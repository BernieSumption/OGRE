package com.berniecode.ogre.enginelib.shared;

import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;


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
		//TODO clone array
		return entityTypes;
	}

	public String getTypeDomainId() {
		return typeDomainId;
	}

	public EntityType getEntityTypeByName(String entityTypeName) throws NoSuchThingException {
		for (int i=0; i<entityTypes.length; i++) {
			if (entityTypes[i].getName().equals(entityTypeName)) {
				return entityTypes[i];
			}
		}
		throw new NoSuchThingException("Type domain " + typeDomainId + " has no EntityType called '" + entityTypeName + "'");
	}

}
