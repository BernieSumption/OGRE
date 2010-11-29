package com.berniecode.ogre.engine.client;

import com.berniecode.ogre.engine.platformhooks.NativeOrderedCollection;
import com.berniecode.ogre.engine.shared.EntityType;
import com.berniecode.ogre.engine.shared.OrderedCollection;
import com.berniecode.ogre.engine.shared.TypeDomain;

public class MockTypeDomain implements TypeDomain {

	@Override
	public OrderedCollection<EntityType> getEntityTypes() {
		// TODO use JMock to mock out these interfaces
		return new NativeOrderedCollection<EntityType>();
	}

	@Override
	public String getTypeDomainId() {
		return "MockTypeDomain";
	}

}
