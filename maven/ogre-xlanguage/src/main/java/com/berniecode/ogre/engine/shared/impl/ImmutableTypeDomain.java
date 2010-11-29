package com.berniecode.ogre.engine.shared.impl;

import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.TypeDomain;

/**
 * A TypeDomain that is fixed after construction (for sufficiently small values of "fixed")
 * 
 * @author Bernie Sumption
 * 
 * @jtoxNative - not translated into other languages
 */
public class ImmutableTypeDomain implements TypeDomain {

	private final OrderedCollection<EntityType> entityTypes;
	private final String typeDomainId;

	public ImmutableTypeDomain(OrderedCollection<EntityType> entityTypes, String typeDomainId) {
		this.entityTypes = entityTypes;
		this.typeDomainId = typeDomainId;
	}

	@Override
	public OrderedCollection<EntityType> getEntityTypes() {
		return entityTypes;
	}

	@Override
	public String getTypeDomainId() {
		return typeDomainId;
	}

}
