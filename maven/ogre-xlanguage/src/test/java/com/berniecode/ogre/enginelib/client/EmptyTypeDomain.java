package com.berniecode.ogre.enginelib.client;

import com.berniecode.ogre.enginelib.platformhooks.NoSuchThingException;
import com.berniecode.ogre.enginelib.shared.EntityType;
import com.berniecode.ogre.enginelib.shared.TypeDomain;


public class EmptyTypeDomain implements TypeDomain {

	public EntityType[] getEntityTypes() {
		return new EntityType[0];
	}

	public String getTypeDomainId() {
		return "com.berniecode.ogre.EmptyTypeDomain";
	}

	public EntityType getEntityTypeByName(String entityTypeName) throws NoSuchThingException {
		throw new NoSuchThingException("EmptyTypeDomain does not contain the EntityType '" + entityTypeName + "', or indeed any EntityType");
	}

}
