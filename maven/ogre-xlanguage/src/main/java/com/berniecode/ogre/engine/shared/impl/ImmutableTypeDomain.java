package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.ImmutableOrderedCollection;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * A simple implementation of the {@link TypeDomain} interface for which all values must be provided
 * in the constructor
 * 
 * @author Bernie Sumption
 */
public class ImmutableTypeDomain implements TypeDomain {

	private final OrderedCollection entityTypes;
	private final String typeDomainId;

	public ImmutableTypeDomain(String typeDomainId, OrderedCollection entityTypes) {
		this.typeDomainId = typeDomainId;
		this.entityTypes = new ImmutableOrderedCollection(entityTypes);
	}

	public OrderedCollection getEntityTypes() {
		return entityTypes;
	}

	public String getTypeDomainId() {
		return typeDomainId;
	}

}
